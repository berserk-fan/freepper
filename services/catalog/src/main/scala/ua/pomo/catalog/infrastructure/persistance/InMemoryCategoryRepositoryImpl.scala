package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{MonadCancelThrow, Ref}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.category._

import java.util.UUID
import scala.collection.mutable
import monocle.syntax.all._
import shapeless._

class InMemoryCategoryRepositoryImpl[F[_]: MonadCancelThrow] private[persistance] (
    ref: Ref[F, Map[CategoryUUID, Category]])
    extends CategoryRepository[F] {
  override def create(category: CreateCategory): F[CategoryUUID] = ref.modify { map =>
    val catUUID = CategoryUUID(UUID.randomUUID())
    val category1 = Category(
      catUUID,
      category.readableId,
      category.displayName,
      category.description
    )
    (map + (catUUID -> category1), catUUID)
  }

  override def get(id: CategoryUUID): F[Category] = {
    find(id)
      .flatMap(_.fold(new Exception(s"category id $id not found").raiseError[F, Category])(_.pure[F]))
  }

  override def find(id: CategoryUUID): F[Option[Category]] = ref.get.map(_.get(id))

  override def update(req: UpdateCategory): F[Int] = ref.modify { map =>
    object updateObj extends InMemoryUpdaterPoly[Category] {
      val a: Res[CategoryDisplayName] = gen(_.focus(_.displayName))
    }
    map.get(req.id).fold((map, 0)) { category =>
      var updated = category.copy()
      req.readableId.foreach { rId =>
        updated = updated.copy(readableId = rId)
      }
      req.displayName.foreach(dName => updated = updated.copy(displayName = dName))
      req.description.foreach(descr => updated = updated.copy(description = descr))
      (map + (updated.id -> updated), 1)
    }
  }

  override def delete(id: CategoryUUID): F[Unit] = ref.update(map => map.get(id).fold(map)(map - _.id))

  override def query(req: CategoryQuery): F[List[Category]] = ref.get.map { map =>
    val filter = req.selector match {
      case CategorySelector.RidIs(rid) =>
        (x: Category) =>
          x.readableId == rid
      case CategorySelector.UidIs(uid) =>
        (x: Category) =>
          x.id == uid
      case CategorySelector.All =>
        (_: Category) =>
          true
    }
    val offset = req.token.offset.toInt
    val limit = req.token.size.toInt
    map.values.filter(filter).slice(offset, offset + limit).toList
  }
}
