package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{IO, MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import cats.~>
import doobie.{ConnectionIO, Transactor}
import doobie.implicits._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl

class CategoryServiceImpl[F[_]: MonadCancelThrow, G[_]: Sync] private (xa: G ~> F, repository: CategoryRepository[G])
    extends CategoryService[F] {
  override def get(id: CategoryUUID): F[Category] = {
    repository
      .find(id)
      .flatMap(_.fold(NotFound("category", id).raiseError[G, Category])(_.pure[G]))
      .mapK(xa)
  }

  override def findAll(): F[List[Category]] = {
    repository.findAll().mapK(xa)
  }

  override def update(req: UpdateCategory): F[Category] = {
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
      .mapK(xa)
  }

  override def delete(id: CategoryUUID): F[Unit] = {
    repository.delete(id).mapK(xa)
  }

  override def create(category: CreateCategory): F[Category] = {
    repository
      .create(category)
      .flatMap { id =>
        repository.get(id)
      }
      .mapK(xa)
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
