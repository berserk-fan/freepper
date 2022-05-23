package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import shapeless._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.product._

import java.util.UUID

class ProductRepositoryImpl private () extends ProductRepository[ConnectionIO] {

  import ProductRepositoryImpl._

  override def create(command: CreateProduct): ConnectionIO[ProductId] = {
    Queries.create(command).withUniqueGeneratedKeys[ProductId]("id")
  }

  override def get(id: ProductId): ConnectionIO[Product] = {
    OptionT(find(id)).getOrElseF(NotFound("product", id).raiseError[ConnectionIO, Product])
  }

  override def find(id: ProductId): ConnectionIO[Option[Product]] = {
    OptionT(Queries.find(ProductQuery(PageToken.NonEmpty(2L, 0L), ProductSelector.IdIs(id))).option).value
  }

  override def query(query: ProductQuery): ConnectionIO[List[Product]] = {
    Queries.find(query).to[List]
  }

  override def update(command: UpdateProduct): ConnectionIO[Int] = {
    Queries.update(command).run
  }

  override def delete(id: ProductId): ConnectionIO[Int] = {
    Queries.delete(id).run
  }
}

object ProductRepositoryImpl {
  def apply(): ProductRepository[ConnectionIO] = {
    new ProductRepositoryImpl()
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
            (price, promo_price, image_list_id, model_id, parameter_ids)
        VALUES ($priceUsd,
                $promoPriceUsd, 
                $imageListId, 
                $modelId, 
                ${parameterIds.map(_.value)})
         """.update
    }

    def update(command: UpdateProduct): Update0 = {
      object updaterPoly extends DbUpdaterPoly {
        implicit val modelUUID: Res[ModelId] = gen("model_id")
        implicit val imageListId: Res[ImageListId] = gen("image_list_id")
        implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price")
        implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price")
      }
      val setters = Fragments.setOpt(Generic[UpdateProduct].to(command).drop(1).map(updaterPoly).toList: _*)
      sql"update products $setters where id=${command.id}".update
    }

    private case class GetProductDto(
        productId: ProductId,
        modelId: ModelId,
        categoryId: CategoryUUID,
        modelDisplayName: ModelDisplayName,
        price: ProductStandardPrice,
        promoPrice: Option[ProductPromoPrice]
    )

    def find(query: ProductQuery): Query0[Product] = {
      implicit val readParamList: Get[List[ParameterId]] = Get[List[UUID]].map(_.map(ParameterId.apply))
      implicit val readParamDisplayNames: Get[List[ParameterDisplayName]] =
        Get[List[String]].map(_.map(ParameterDisplayName.apply))
      implicit val readListImage: Get[List[Image]] = jsonAggListJson[Image]
      val sql =
        sql"""
            select p.id, m.id, m.category_id, m.display_name, p.price, p.promo_price,
                   il.id, il.display_name, 
                   case
                     when count(i.id) = 0
                     then '[]'
                     else json_agg(json_build_object('id', i.id, 'src', i.src, 'alt', i.alt) ORDER BY i.list_order)
                   end,
                   p.parameter_ids,
                   (
                       select COALESCE(array_agg(ps.display_name), ARRAY[]::VARCHAR[]) 
                       from unnest(p.parameter_ids) pid left join parameters ps on ps.id = pid
                   ) as parameter_display_names
            from products p 
                left join models m on p.model_id = m.id 
                left join image_lists il on p.image_list_id = il.id
                join images i on il.id = i.image_list_id
            where ${compile("p", query.selector)}
            group by p.id, m.id, il.id
            order by p.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
      sql
        .query[(GetProductDto, ImageList, List[ParameterId], List[ParameterDisplayName])]
        .map { res =>
          val (product, imageList, params, parameterDisplayNames) = res

          Product(
            product.productId,
            product.modelId,
            Product.makeDisplayName(product.modelDisplayName, parameterDisplayNames),
            product.categoryId,
            imageList,
            ProductPrice(product.price, product.promoPrice),
            params
          )
        }
    }
  }
}
