package com.freepper.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.implicits.toFunctorOps
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{ConnectionIO, Fragment, Fragments, Get}
import monocle.syntax.all._
import shapeless.Generic
import com.freepper.catalog.domain.category.CategoryId
import com.freepper.catalog.domain.imageList.{ImageList, ImageListDisplayName, ImageListId}
import com.freepper.catalog.domain.model.{ModelDisplayName, ModelId}
import com.freepper.catalog.domain.product.{Product, ProductId, ProductRepository, ProductSelector, _}
import com.freepper.catalog.domain.product
import com.freepper.common.domain.crud
import com.freepper.common.domain.error.DbErr
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import com.freepper.common.infrastracture.persistance.postgres.{
  AbstractPostgresRepository,
  DbUpdaterPoly,
  Queries,
  QueryHelpers
}

import java.util.UUID

object ProductRepository {

  def inmemory[F[_]: Sync]: F[ProductRepository[F]] = {
    Ref.of[F, Map[ProductId, product.Product]](Map()).map { ref =>
      new ProductInMemoryRepositoryImpl[F](ref)
    }
  }

  def postgres: ProductRepository[ConnectionIO] = {
    new ProductRepositoryImpl()
  }

  object ProductQueries extends Queries[ProductCrud] {
    override def create(req: CreateProduct): List[doobie.Update0] = List {
      import req._
      val id = req.id

      val sql =
        sql"""
          INSERT INTO products 
              (id, price, promo_price, image_list_id, model_id, parameter_ids)
          VALUES ($id,
                  $priceUsd,
                  $promoPriceUsd, 
                  $imageListId, 
                  $modelId, 
                  ${parameterIds.map(_.value)})
           """.update
      sql
    }

    override def delete(id: ProductId): List[doobie.Update0] = List {
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
        categoryId: CategoryId,
        modelDisplayName: ModelDisplayName,
        price: ProductStandardPrice,
        promoPrice: Option[ProductPromoPrice]
    )

    override def find(query: crud.Query[ProductSelector]): doobie.Query0[product.Product] = {
      implicit val readListImage: Get[product.Product] = readJsonFromView[product.Product]
      val limitOffset = compileToken(query.page)
      val sql1 =
        sql"""
              select p.json
              from products_prebuilt p
              where ${compile("p", query.selector)}
              order by p.create_time
              $limitOffset
           """
      sql1
        .query[product.Product]
    }

    private object updaterPoly extends DbUpdaterPoly {
      implicit val modelUUID: Res[ModelId] = gen("model_id")
      implicit val imageListId: Res[ImageListId] = gen("image_list_id")
      implicit val productStandardPrice: Res[ProductStandardPrice] = gen("price")
      implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen("promo_price")
    }

    override def update(req: UpdateProduct): List[doobie.Update0] = {
      QueryHelpers.defaultUpdateRaw(Generic[UpdateProduct].to(req), req.id, updaterPoly, "products").toList.map(_.update)
    }
  }

  private class ProductRepositoryImpl extends AbstractPostgresRepository[ProductCrud](ProductQueries) {
    override protected def findQuery: ProductId => ProductSelector = ProductSelector.IdIs.apply
  }

  private case class ProductInMemoryRepositoryImpl[F[_]: Sync](ref: Ref[F, Map[ProductId, product.Product]])
      extends AbstractInMemoryRepository[F, ProductCrud](ref) {
    private object update extends InMemoryUpdaterPoly[product.Product] {
      implicit val modelUUID: Res[ModelId] = gen(_.focus(_.modelId))
      implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
      implicit val productStandardPrice: Res[ProductStandardPrice] = gen(_.focus(_.price.standard))
      implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen(_.focus(_.price.promo))
    }

    override def update(command: UpdateProduct): F[Int] = {
      updateHelper(command, update, Generic[UpdateProduct])
    }

    override protected def creator: CreateProduct => product.Product = (command: CreateProduct) => {
      val id = ProductId(UUID.randomUUID())
      Product(
        id,
        command.modelId,
        ProductDisplayName(""),
        CategoryId(UUID.randomUUID()),
        ImageList(command.imageListId, ImageListDisplayName(""), Nil),
        ProductPrice(command.priceUsd, command.promoPriceUsd),
        command.parameterIds
      )
    }

    override protected def filter: ProductSelector => product.Product => Boolean = {
      case ProductSelector.All              => _ => true
      case ProductSelector.IdIs(id)         => _.id == id
      case ProductSelector.IdIn(ids)        => p => ids.exists(_ == p.id)
      case ProductSelector.ModelIs(modelId) => _.modelId == modelId
    }
  }

}
