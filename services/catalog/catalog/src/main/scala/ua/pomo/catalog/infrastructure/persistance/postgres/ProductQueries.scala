package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie.{Fragment, Fragments, Get}
import shapeless.Generic
import doobie.postgres.implicits._
import doobie.implicits._

import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image.Image
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListId}
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}
import ua.pomo.catalog.domain.parameter.{ParameterDisplayName, ParameterId}
import ua.pomo.catalog.domain.product._
import ua.pomo.common.domain.repository
import ua.pomo.common.infrastracture.persistance.postgres.{DbUpdaterPoly, Queries, QueriesHelpers}

import java.util.UUID

object ProductQueries extends Queries[ProductCrud] {
  override def create(req: CreateProduct): (doobie.Update0, ProductId) = {
    import req._
    val id = ProductId(UUID.randomUUID())

    val sql = sql"""
        INSERT INTO products 
            (id, price, promo_price, image_list_id, model_id, parameter_ids)
        VALUES ($id,
                $priceUsd,
                $promoPriceUsd, 
                $imageListId, 
                $modelId, 
                ${parameterIds.map(_.value)})
         """.update
    (sql, id)
  }

  override def delete(id: ProductId): doobie.Update0 = {
    sql"delete from products where id=$id".update
  }

  private def compile(productTable: String, idOpt: ProductSelector): Fragment = {
    val p: Fragment = Fragment.const0(productTable)
    idOpt match {
      case ProductSelector.All              => fr"1 = 1"
      case ProductSelector.IdIs(id)         => fr"$p.id = $id"
      case ProductSelector.IdIn(ids)        => Fragments.in(fr"$p.id", ids)
      case ProductSelector.ModelIs(modelId) => fr"$p.model_id = $modelId"
    }
  }

  private case class GetProductDto(
      productId: ProductId,
      modelId: ModelId,
      categoryId: CategoryUUID,
      modelDisplayName: ModelDisplayName,
      price: ProductStandardPrice,
      promoPrice: Option[ProductPromoPrice]
  )

  override def find(query: repository.Query[ProductSelector]): doobie.Query0[Product] = {
    implicit val readListImage: Get[List[Image]] = jsonAggListJson[Image]
    val sql1 =
      sql"""
            select p.id, m.id, m.category_id, m.display_name, p.price, p.promo_price,
                   il.id, il.display_name, 
                   (${DeprecatedMethods.jsonList("p.image_list_id")}),
                   p.parameter_ids,
                   (
                       select COALESCE(array_agg(ps.display_name), ARRAY[]::VARCHAR[]) 
                       from unnest(p.parameter_ids) pid left join parameters ps on ps.id = pid
                   ) as parameter_display_names
            from products p 
                left join models m on p.model_id = m.id 
                left join image_lists il on p.image_list_id = il.id
            where ${compile("p", query.selector)}
            order by p.create_time
            limit ${query.page.size}
            offset ${query.page.offset}
         """
    sql1
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

  private object updaterPoly extends DbUpdaterPoly {
    implicit val modelUUID: Res[ModelId] = gen("model_id")
    implicit val imageListId: Res[ImageListId] = gen("image_list_id")
    implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price")
    implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price")
  }

  override def update(req: UpdateProduct): doobie.Update0 = {
    QueriesHelpers[ProductCrud]().updateQHelper(req, updaterPoly, "products", Generic[UpdateProduct])
  }
}
