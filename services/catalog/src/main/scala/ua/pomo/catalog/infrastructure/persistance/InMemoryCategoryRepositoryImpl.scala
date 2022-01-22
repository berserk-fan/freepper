package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{MonadCancelThrow, Ref}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.category._

import java.util.UUID
import scala.collection.mutable

class InMemoryCategoryRepositoryImpl[F[_]: MonadCancelThrow] private[persistance] (
    ref: Ref[F, mutable.Map[CategoryId, Category]])
    extends CategoryRepository[F] {
  override def create(category: Category): F[CategoryUUID] = ref.modify { map =>
    val catUUID = CategoryUUID(UUID.randomUUID())
    val catId = CategoryId(catUUID)
    (map.addOne(catId -> category.copy(id = catUUID)), catUUID)
  }

  override def get(id: CategoryId): F[Category] = {
    find(id)
      .flatMap(_.fold(new Exception(s"category id $id not found").raiseError[F, Category])(_.pure[F]))
  }

  override def find(id: CategoryId): F[Option[Category]] = ref.get.map(_.get(id))

  override def findAll(): F[List[Category]] = ref.get.map(_.values.toList)

  override def update(req: UpdateCategory): F[Int] = ref.modify { map =>
    val res = map.get(req.id).fold(0) { category =>
      var updated = category.copy()
      req.readableId.foreach { rId =>
        updated = updated.copy(readableId = rId)
      }
      req.displayName.foreach(dName => updated = updated.copy(displayName = dName))
      req.description.foreach(descr => updated = updated.copy(description = descr))
      map.addOne((CategoryId(updated.id), updated))
      1
    }
    (map, res)
  }

  override def delete(id: CategoryId): F[Unit] = ref.update(map => { map.remove(id); map })
}
