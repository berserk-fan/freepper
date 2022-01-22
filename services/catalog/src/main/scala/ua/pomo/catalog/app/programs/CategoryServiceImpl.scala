package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{IO, MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import doobie.{ConnectionIO, Transactor}
import doobie.implicits._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl

class CategoryServiceImpl[F[_]: MonadCancelThrow, G[_]: Sync] private (xa: FunctionK[G, F],
                                                                       repository: CategoryRepository[G])
    extends CategoryService[F] {
  override def get(id: CategoryId): F[Category] = {
    repository
      .get(id)
      .toF
  }

  override def findAll(): F[List[Category]] = {
    repository.findAll().toF
  }

  override def updateCategory(req: UpdateCategory): F[Category] = {
    repository
      .update(req)
      .flatMap { count =>
        if (count > 0) {
          repository.find(req.id).flatMap {
            case Some(updatedCategory) => updatedCategory.pure[G]
            case None =>
              new IllegalStateException("no category after non zero update")
                .raiseError[G, Category]
          }
        } else {
          new RuntimeException("category not found")
            .raiseError[G, Category]
        }
      }
      .toF
  }

  override def deleteCategory(id: CategoryId): F[Unit] = {
    repository.delete(id).toF
  }

  override def createCategory(category: Category): F[Category] = {
    repository
      .create(category)
      .flatMap { id =>
        repository.get(CategoryId(id))
      }
      .toF
  }

  implicit class TransactOps[T](t: G[T]) {
    def toF: F[T] = xa.apply(t)
  }
}

object CategoryServiceImpl {
  def apply[F[_]: MonadCancelThrow](xa: Transactor[F],
                                    categoryRepository: CategoryRepository[ConnectionIO]): CategoryService[F] = {
    new CategoryServiceImpl[F, ConnectionIO](xa.trans, categoryRepository)
  }

  def makeInMemory[F[_]: Sync]: F[CategoryService[F]] = {
    CategoryRepositoryImpl
      .makeInMemory[F]
      .map(
        new CategoryServiceImpl[F, F](FunctionK.id[F], _)
      )
  }
}
