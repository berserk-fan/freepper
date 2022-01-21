package ua.pomo.catalog.app.programs

import cats.effect.MonadCancelThrow
import cats.implicits.{
  catsSyntaxApplicativeErrorId,
  catsSyntaxApplicativeId,
  toFlatMapOps
}
import doobie.{ConnectionIO, Transactor}
import doobie.implicits._
import ua.pomo.catalog.domain.category._

class CategoryServiceImpl[F[_]: MonadCancelThrow] private (
    xa: Transactor[F],
    repository: CategoryRepository[ConnectionIO])
    extends CategoryService[F] {
  override def getCategory(id: CategoryId): F[Category] = {
    repository
      .get(id)
      .transact(xa)
  }

  override def findAll(): F[List[Category]] = {
    repository.findAll().transact(xa)
  }

  override def updateCategory(req: UpdateCategory): F[Category] = {
    repository
      .update(req)
      .flatMap { count =>
        if (count > 0) {
          repository.find(req.id).flatMap {
            case Some(updatedCategory) => updatedCategory.pure[ConnectionIO]
            case None =>
              new IllegalStateException("no category after non zero update")
                .raiseError[ConnectionIO, Category]
          }
        } else {
          new RuntimeException("category not found")
            .raiseError[ConnectionIO, Category]
        }
      }
      .transact(xa)
  }

  override def deleteCategory(id: CategoryId): F[Unit] = {
    repository.delete(id).transact(xa)
  }

  override def createCategory(category: Category): F[Category] = {
    repository
      .create(category)
      .flatMap { id =>
        repository.get(Left(id))
      }
      .transact(xa)
  }
}

object CategoryServiceImpl {
  def apply[F[_]: MonadCancelThrow](
      xa: Transactor[F],
      categoryRepository: CategoryRepository[ConnectionIO])
    : CategoryService[F] = {
    new CategoryServiceImpl[F](xa, categoryRepository)
  }
}
