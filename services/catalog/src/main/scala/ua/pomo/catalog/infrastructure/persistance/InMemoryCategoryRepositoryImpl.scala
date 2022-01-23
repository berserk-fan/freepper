package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{MonadCancelThrow, Ref}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model.{Model, ModelId, ModelUUID}

import java.util.UUID
import scala.collection.mutable

class InMemoryCategoryRepositoryImpl[F[_]: MonadCancelThrow] private[persistance] (
    ref: Ref[F, Map[CategoryUUID, Category]])
    extends CategoryRepository[F] {
  override def create(category: Category): F[CategoryUUID] = ref.modify { map =>
    val catUUID = CategoryUUID(UUID.randomUUID())
    (map + (catUUID -> category.copy(id = catUUID)), catUUID)
  }

  override def get(id: CategoryId): F[Category] = {
    find(id)
      .flatMap(_.fold(new Exception(s"category id $id not found").raiseError[F, Category])(_.pure[F]))
  }

  override def find(id: CategoryId): F[Option[Category]] = ref.get.map(_.findById(id))

  override def findAll(): F[List[Category]] = ref.get.map(_.values.toList)

  override def update(req: UpdateCategory): F[Int] = ref.modify { map =>
    map.findById(req.id).fold((map, 0)) { category =>
      var updated = category.copy()
      req.readableId.foreach { rId =>
        updated = updated.copy(readableId = rId)
      }
      req.displayName.foreach(dName => updated = updated.copy(displayName = dName))
      req.description.foreach(descr => updated = updated.copy(description = descr))
      (map + (updated.id -> updated), 1)
    }
  }

  override def delete(id: CategoryId): F[Unit] = ref.update(map => map.findById(id).fold(map)(map - _.id))

  private implicit class FindById(m: Map[CategoryUUID, Category]) {
    def findById(id: CategoryId): Option[Category] = id.value match {
      case Left(uuid)        => m.get(uuid)
      case Right(readableId) => m.values.find(_.readableId == readableId)
    }
  }
}
