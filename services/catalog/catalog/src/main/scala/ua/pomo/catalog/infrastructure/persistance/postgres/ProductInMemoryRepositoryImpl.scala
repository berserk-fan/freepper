package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.implicits.toFunctorOps
import monocle.syntax.all._
import shapeless._
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListDisplayName, ImageListId}
import ua.pomo.catalog.domain.model.ModelId
import ua.pomo.catalog.domain.product._
import ua.pomo.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}

import java.util.UUID

case class ProductInMemoryRepositoryImpl[F[_]: Sync] private (ref: Ref[F, Map[ProductId, Product]])
    extends AbstractInMemoryRepository[F, ProductCrud](ref) {
  private object update extends InMemoryUpdaterPoly[Product] {
    implicit val modelUUID: Res[ModelId] = gen(_.focus(_.modelId))
    implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
    implicit val productStandardPrice: Res[ProductStandardPrice] = gen(_.focus(_.price.standard))
    implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen(_.focus(_.price.promo))
  }

  override def update(command: UpdateProduct): F[Int] = {
    updateHelper(command, update, Generic[UpdateProduct])
  }

  override def delete(id: ProductId): F[Int] = ref.modify { map =>
    map.get(id).fold((map, 0))(x => (map - x.id, 1))
  }

  override protected def creator: CreateProduct => Product = (command: CreateProduct) => {
    val id = ProductId(UUID.randomUUID())
    Product(
      id,
      command.modelId,
      ProductDisplayName(""),
      CategoryUUID(UUID.randomUUID()),
      ImageList(ImageListId(UUID.randomUUID()), ImageListDisplayName(""), Nil),
      ProductPrice(command.priceUsd, command.promoPriceUsd),
      command.parameterIds
    )
  }

  override protected def filter: ProductSelector => Product => Boolean = {
    case ProductSelector.All              => _ => true
    case ProductSelector.IdIs(id)         => _.id == id
    case ProductSelector.IdIn(ids)        => p => ids.exists(_ == p.id)
    case ProductSelector.ModelIs(modelId) => _.modelId == modelId
  }
}

object ProductInMemoryRepositoryImpl {
  def apply[F[_]: Sync]: F[ProductRepository[F]] = {
    Ref.of[F, Map[ProductId, Product]](Map()).map { ref =>
      new ProductInMemoryRepositoryImpl[F](ref)
    }
  }
}
