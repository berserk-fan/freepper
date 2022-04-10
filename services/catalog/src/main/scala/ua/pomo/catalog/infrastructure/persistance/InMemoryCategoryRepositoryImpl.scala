package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{MonadCancelThrow, Ref}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.category._

import java.util.UUID
import scala.collection.mutable
import monocle.syntax.all._
import shapeless._

class InMemoryCategoryRepositoryImpl[F[_]: MonadCancelThrow] private[persistance] (
    ref: Ref[F, Map[CategoryId, Category]])
    extends CategoryRepository[F] {
  override def create(category: CreateCategory): F[CategoryId] = ref.modify { map =>
    val catUUID = CategoryId(UUID.randomUUID())
    val category1 = Category(
      catUUID,
      category.readableId,
      category.displayName,
      category.description
    )
    (map + (catUUID -> category1), catUUID)
  }

  override def get(id: CategoryId): F[Category] = {
    find(id)
      .flatMap(_.fold(new Exception(s"category id $id not found").raiseError[F, Category])(_.pure[F]))
  }

  override def find(id: CategoryId): F[Option[Category]] = ref.get.map(_.get(id))

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

  override def delete(id: CategoryId): F[Unit] = ref.update(map => map.get(id).fold(map)(map - _.id))

  override def query(req: CategoryQuery): F[List[Category]] = ref.get.map { map =>
    val filter = req.selector match {
      case CategorySelector.IdIs(categoryId) =>
        (x: Category) =>
          x.id == categoryId
      case CategorySelector.All =>
        (x: Category) =>
          true
    }
    val offset = req.token.offset.toInt
    val limit = req.token.size.toInt
    map.values.filter(filter).slice(offset, offset + limit).toList
  }
}
