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
import ua.pomo.catalog.domain.param._
import ua.pomo.catalog.domain.product._

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

  override def delete(id: ProductId): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
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
            (price_usd, promo_price_usd, image_list_id, model_id, parameters)
        VALUES ($priceUsd, 
                $promoPriceUsd, 
                $imageListId, 
                $modelId, 
                ${parameters.map(_.value)})
         """.update
    }

    def update(command: UpdateProduct): Update0 = {
      object updaterPoly extends DbUpdaterPoly {
        implicit val modelUUID: Res[ModelId] = gen("model_id")
        implicit val imageListId: Res[ImageListId] = gen("image_list_id")
        implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price_usd")
        implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price_usd")
      }
      val setters = Fragments.setOpt(Generic[UpdateProduct].to(command).drop(1).map(updaterPoly).toList: _*)
      sql"update products $setters where id=${command.id}".update
    }

    private case class GetProductDto(productId: ProductId,
                                     modelId: ModelId,
                                     categoryId: CategoryUUID,
                                     modelDisplayName: ModelDisplayName,
                                     price: ProductStandardPrice,
                                     promoPrice: Option[ProductPromoPrice])

    def find(query: ProductQuery): Query0[Product] = {
      implicit val readParamList: Get[List[Parameter]] = jsonAggListJson[Parameter]
      implicit val readListImage: Get[List[Image]] = jsonAggListJson[Image]
      sql"""
            select p.id, m.id, m.category_id, m.display_name, p.price_usd, p.promo_price_usd,
                   il.id, il.display_name, 
                   case
                     when count(i.id) = 0
                     then '[]'
                     else json_agg(json_build_object('id', i.id, 'src', i.src, 'alt', i.alt))
                   end,
                   COALESCE((
                    select json_agg(('id', par.id, 
                                     'parameterListId', par.parameter_list_id, 
                                     'displayName', par.display_name, 
                                     'image', json_build_object('id', i.id, 
                                                                'src', i.src, 
                                                                'alt', i.alt)))
                    from parameters par 
                        inner join unnest(p.parameters) param_id on par.id = param_id 
                        left join images i on par.image_id = i.id
                   ), '[]')
            from products p 
                left join models m on p.model_id = m.id 
                left join image_lists il on m.image_list_id = il.id
                join images i on il.id = i.image_list_id
            where ${compile("p", query.selector)}
            group by p.id, m.id, il.id
            order by p.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
        .query[(GetProductDto, ImageList, List[Parameter])]
        .map { res =>
          val (product, imageList, params) = res
          val displayName = Product.createDisplayName(product.modelDisplayName, params)
          Product(product.productId,
                  product.modelId,
                  product.categoryId,
                  displayName,
                  imageList,
                  ProductPrice(product.price, product.promoPrice),
                  params)
        }
    }
  }
}
