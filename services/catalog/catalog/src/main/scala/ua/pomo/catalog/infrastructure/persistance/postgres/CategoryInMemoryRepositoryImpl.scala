package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{MonadCancelThrow, Ref}
import shapeless._
import ua.pomo.catalog.domain.category._
import ua.pomo.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}

import java.util.UUID
import monocle.syntax.all._

case class CategoryInMemoryRepositoryImpl[F[_]: MonadCancelThrow] private[persistance] (
    ref: Ref[F, Map[CategoryUUID, Category]]
) extends AbstractInMemoryRepository[F, CategoryCrud](ref) {
  override protected def creator: CreateCategory => Category = (category: CreateCategory) => {
    val catUUID = CategoryUUID(UUID.randomUUID())
    Category(
      catUUID,
      category.readableId,
      category.displayName,
      category.description
    )
  }

  override protected def filter: CategorySelector => Category => Boolean = {
    case CategorySelector.RidIs(rid) => (x: Category) => x.readableId == rid
    case CategorySelector.UidIs(uid) => (x: Category) => x.id == uid
    case CategorySelector.All        => (_: Category) => true
  }

  private object updaterObj extends InMemoryUpdaterPoly[Category] {
    implicit val readableId: Res[CategoryReadableId] = gen(_.focus(_.readableId))
    implicit val displayName: Res[CategoryDisplayName] = gen(_.focus(_.displayName))
    implicit val description: Res[CategoryDescription] = gen(_.focus(_.description))
  }

  override def update(req: UpdateCategory): F[Int] = {
    updateHelper(req, updaterObj, Generic[UpdateCategory])
  }
}
