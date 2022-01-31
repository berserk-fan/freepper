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
import ua.pomo.catalog.domain.image.{Image, ImageAlt, ImageId, ImageList, ImageListDisplayName, ImageListId, ImageListQuery, ImageListRepository, ImageListSelector, ImageSrc}
import shapeless._

class ProductRepositoryImpl private(imageListRepo: ImageListRepository[ConnectionIO]) extends ProductRepository[ConnectionIO] {

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
        case ProductSelector.All => fr"1 = 1"
        case ProductSelector.IdIs(id) => fr"$p.id = $id"
        case ProductSelector.IdIn(ids) => Fragments.in(fr"$p.id", ids)
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
        VALUES ($priceUsd, $promoPriceUsd, $imageListId, $modelId, $fabricId, $sizeId)
         """.update
    }

    def update(command: UpdateProduct): Update0 = {
      object updaterPoly extends DbUpdaterPoly {
        implicit val modelUUID: Res[ModelId] = gen("model_id")
        implicit val fabricUUID: Res[FabricUUID] = gen("fabric_id")
        implicit val sizeUUID: Res[SizeUUID] = gen("size_id")
        implicit val imageListId: Res[ImageListId] = gen("image_list_id")
        implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price_usd")
        implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price_usd")
      }
      val setters = Fragments.setOpt(Generic[UpdateProduct].to(command).drop(1).map(updaterPoly).toList: _*)
      sql"update products $setters where id=${command.id}".update
    }

    def get(query: ProductQuery): Query0[Product] = {
      sql"""
            select p.id, p.model_id, m.display_name, f.id, f.display_name, i.id, i.src, i.alt, s.id, s.display_name, p.image_list_id, p.price_usd, p.promo_price_usd
            from products p
                left join fabrics f on p.fabric_id = f.id
                left join sizes s on p.size_id = s.id
                left join images i on f.image_id = i.id
                left join models m on p.model_id = m.id
            where ${compile("p", query.selector)}
            order by p.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
        .query[(ProductId,
          ModelId,
          ModelDisplayName,
          FabricUUID,
          FabricDisplayName,
          ImageId,
          ImageSrc,
          ImageAlt,
          SizeUUID,
          SizeDisplayName,
          ImageListId,
          ProductStandardPrice,
          Option[ProductPromoPrice])]
        .map { res =>
          val fabric = Fabric(res._4, res._5, Image(res._6, res._7, res._8))
          val size = Size(res._9, res._10)
          val imageList = ImageList(res._11, ImageListDisplayName(""), Nil)
          val displayName = Product.createDisplayName(res._3, fabric.displayName, size.displayName)
          Product(res._1, res._2, displayName, fabric, size, imageList, ProductPrice(res._12, res._13))
        }
    }
  }
}
