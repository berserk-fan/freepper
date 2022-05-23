package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image.{Image, ImageAlt, ImageList, ImageListDisplayName, ImageListId, ImageSrc}
import ua.pomo.catalog.domain.model.ModelId
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.parameter._
import monocle.syntax.all._
import shapeless._
import ua.pomo.catalog.domain.category.{CategoryUUID}

import java.util.UUID

class InMemoryProductRepositoryImpl[F[_]: Sync] private (ref: Ref[F, Map[ProductId, Product]])
    extends ProductRepository[F] {
  override def create(command: CreateProduct): F[ProductId] = ref.modify { map =>
    val id = ProductId(UUID.randomUUID())
    val res = Product(
      id,
      command.modelId,
      ProductDisplayName(""),
      CategoryUUID(UUID.randomUUID()),
      ImageList(ImageListId(UUID.randomUUID()), ImageListDisplayName(""), Nil),
      ProductPrice(command.priceUsd, command.promoPriceUsd),
      command.parameterIds
    )
    (map + (id -> res), id)
  }

  override def get(id: ProductId): F[Product] = {
    OptionT(find(id)).getOrElseF(NotFound("product", id).raiseError[F, Product])
  }

  override def find(id: ProductId): F[Option[Product]] =
    query(ProductQuery(PageToken.NonEmpty(1, 0), ProductSelector.IdIs(id))).map(_.headOption)

  override def query(req: ProductQuery): F[List[Product]] = {
    val filter: Product => Boolean = req.selector match {
      case ProductSelector.All =>
        _ =>
          true
      case ProductSelector.IdIs(id) => _.id == id
      case ProductSelector.IdIn(ids) =>
        p =>
          ids.exists(_ == p.id)
      case ProductSelector.ModelIs(modelId) => _.modelId == modelId
    }
    ref.get.map(_.values.filter(filter).toList)
  }

  override def update(command: UpdateProduct): F[Int] = ref.modify { map =>
    object update extends InMemoryUpdaterPoly[Product] {
      implicit val modelUUID: Res[ModelId] = gen(_.focus(_.modelId))
      implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
      implicit val productStandardPrice: Res[ProductStandardPrice] = gen(_.focus(_.price.standard))
      implicit val productPromoPrice: Res[Option[ProductPromoPrice]] = gen(_.focus(_.price.promo))
    }
    val updater = Generic[UpdateProduct].to(command).drop(Nat._1).map(update).toList.flatten.reduce(_ andThen _)
    map.get(command.id).fold((map, 0))(x => (map + (command.id -> updater(x)), 1))
  }

  override def delete(id: ProductId): F[Int] = ref.modify { map =>
    map.get(id).fold((map, 0))(x => (map - x.id, 1))
  }
}

object InMemoryProductRepositoryImpl {
  def apply[F[_]: Sync]: F[ProductRepository[F]] = {
    Ref.of[F, Map[ProductId, Product]](Map()).map { ref =>
      new InMemoryProductRepositoryImpl[F](ref)
    }
  }
}
