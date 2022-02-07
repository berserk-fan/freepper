package ua.pomo.catalog.infrastructure.persistance

import cats.data.{NonEmptyList, OptionT}
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFunctorOps, toTraverseOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import squants.market.MoneyConversions.fromDouble
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.error.{DbErr, NotFound}
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.param._
import ua.pomo.catalog.domain.image._
import shapeless._
import shapeless.record.Record
import ua.pomo.catalog.domain.category.CategoryUUID

class ProductRepositoryImpl private (imageListRepo: ImageListRepository[ConnectionIO])
    extends ProductRepository[ConnectionIO] {

  import ProductRepositoryImpl._

  override def create(command: CreateProduct): ConnectionIO[ProductId] = {
    Queries.create(command).withUniqueGeneratedKeys[ProductId]("id")
  }

  override def get(id: ProductId): ConnectionIO[Product] = {
    OptionT(find(id)).getOrElseF(NotFound("product", id).raiseError[ConnectionIO, Product])
  }

  override def find(id: ProductId): ConnectionIO[Option[Product]] = {
    val res = for {
      product <- OptionT(Queries.get(ProductQuery(PageToken.NonEmpty(2L, 0L), ProductSelector.IdIs(id))).option)
      imageList <- OptionT.liftF(imageListRepo.get(product.imageList.id))
    } yield product.copy(imageList = imageList)
    res.value
  }

  override def query(query: ProductQuery): ConnectionIO[List[Product]] =
    for {
      products <- Queries.get(query).to[List]
      imageLists <- NonEmptyList
        .fromList(products.map(_.imageList.id))
        .fold[ConnectionIO[List[ImageList]]](List[ImageList]().pure[ConnectionIO]) { imageListIds =>
          imageListRepo.query(
            ImageListQuery(PageToken.NonEmpty(imageListIds.size.toLong, 0), ImageListSelector.IdsIn(imageListIds)))
        }
      imageListsMap = imageLists.groupBy(_.id).flatMap { case (id, imageLists) => imageLists.headOption.map((id, _)) }
      productsWithImageLists <- products.traverse { product =>
        imageListsMap
          .get(product.imageList.id)
          .fold(DbErr(s"imageList for product ${product.id} was not found").raiseError[ConnectionIO, Product]) {
            imageList =>
              product.copy(imageList = imageList).pure[ConnectionIO]
          }
      }
    } yield productsWithImageLists

  override def update(command: UpdateProduct): ConnectionIO[Int] = {
    Queries.update(command).run
  }

  override def delete(id: ProductId): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
  }
}

object ProductRepositoryImpl {
  def apply(imageListRepository: ImageListRepository[ConnectionIO]): ProductRepository[ConnectionIO] = {
    new ProductRepositoryImpl(imageListRepository)
  }

  private[persistance] object Queries {
    private def compile(productTable: String, idOpt: ProductSelector): Fragment = {
      val p: Fragment = Fragment.const0(productTable)
      idOpt match {
        case ProductSelector.All              => fr"1 = 1"
        case ProductSelector.IdIs(id)         => fr"$p.id = $id"
        case ProductSelector.IdIn(ids)        => Fragments.in(fr"$p.id", ids)
        case ProductSelector.ModelIs(modelId) => fr"$p.model_id = $modelId"
      }
    }

    def delete(id: ProductId): Update0 = {
      sql"delete from products where id=$id".update
    }

    def create(command: CreateProduct): Update0 = {
      import command._
      sql"""
        INSERT INTO products 
            (price_usd, promo_price_usd, image_list_id, model_id, fabric_id, size_id)
        VALUES ($priceUsd, 
                $promoPriceUsd, 
                $imageListId, 
                $modelId, 
                ${parameters(ProductParameterKind.Fabric)}, 
                ${parameters(ProductParameterKind.Size)}
         """.update
    }

    def update(command: UpdateProduct): Update0 = {
      object updaterPoly extends DbUpdaterPoly {
        implicit val modelUUID: Res[ModelId] = gen("model_id")
        implicit val fabricUUID: Res[Map[ProductParameterKind, ParameterId]] = ???
        implicit val imageListId: Res[ImageListId] = gen("image_list_id")
        implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price_usd")
        implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price_usd")
      }
      val setters = Fragments.setOpt(Generic[UpdateProduct].to(command).drop(1).map(updaterPoly).toList: _*)
      sql"update products $setters where id=${command.id}".update
    }

    private case class GetParam(id: ParameterId,
                                name: ParameterDisplayName,
                                imageId: ImageId,
                                imageSrc: ImageSrc,
                                imageAlt: ImageAlt)

    private case class GetProduct(productId: ProductId,
                                  modelId: ModelId,
                                  categoryId: CategoryUUID,
                                  displayName: ModelDisplayName,
                                  imageListId: ImageListId,
                                  productPrice: ProductStandardPrice,
                                  productPromoPrice: Option[ProductPromoPrice])

    type GetQuery = Record.`'product -> GetProduct, 'fabric -> Param, 'size -> Param`.T

    def get(query: GetProduct): Query0[Product] = {
      sql"""
            select p.id, p.model_id, m.category_id, m.display_name,
                   p.image_list_id, p.price_usd, p.promo_price_usd,
                   f.id, f.display_name, ifa.id, ifa.src, ifa.alt,
                   s.id, s.display_name, isi.id, isi.src, isi.alt
            from products p
                left join parameters f on p.fabric_id = f.id
                left join parameters s on p.size_id = s.id
                left join images ifa on f.image_id = ifa.id
                left join images isi on s.image_id = isi.id
                left join models m on p.model_id = m.id
            where ${compile("p", query.selector)}
            order by p.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
        .query[GetQuery]
        .map { res =>
          val fabric = res('fabric)
          val size = Parameter(res.sizeId, res.sizeName, Image(res.sizeImageId, res.sizeImageSrc, res.sizeImageAlt))
          val parameters = Map(ProductParameterKind.Size -> size, ProductParameterKind.Fabric -> fabric)
          val displayName = Product.createDisplayName(res._4, parameters.values.toList)
          val imageList = ImageList(res.imageListId, ImageListDisplayName(""), Nil)
          Product(res.productId,
                  res.modelId,
                  res.categoryId,
                  displayName,
                  imageList,
                  parameters,
                  ProductPrice(res._16, res._17))
        }
    }
  }
}
