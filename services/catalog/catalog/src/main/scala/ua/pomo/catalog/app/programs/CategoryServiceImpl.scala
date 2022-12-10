package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import cats.~>
import doobie.{ConnectionIO, Transactor}
import doobie.implicits._
import ua.pomo.catalog.domain.category._
import ua.pomo.common.domain.error.NotFound
import ua.pomo.catalog.infrastructure.persistance.postgres.CategoryRepository

class CategoryServiceImpl[F[_], G[_]: Sync] private (xa: G ~> F, repository: CategoryRepository[G])
    extends CategoryService[F] {
  override def get(id: CategoryUUID): F[Category] = {
    repository
      .find(id)
      .flatMap(_.fold(NotFound("category", id).raiseError[G, Category])(_.pure[G]))
      .mapK(xa)
  }

  override def query(req: CategoryQuery): F[QueryCategoriesResponse] = {
    repository
      .findAll(req)
      .map(cats => QueryCategoriesResponse(cats, computeNextPageToken(req.page, cats)))
      .mapK(xa)
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
    repository.delete(id).as(()).mapK(xa)
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
  def apply[F[_]: MonadCancelThrow](
      xa: Transactor[F],
      categoryRepository: CategoryRepository[ConnectionIO]
  ): CategoryService[F] = {
    new CategoryServiceImpl[F, ConnectionIO](xa.trans, categoryRepository)
  }

  def makeInMemory[F[_]: Sync]: F[CategoryService[F]] = {
    CategoryRepository
      .inmemory[F]
      .map(
        new CategoryServiceImpl[F, F](FunctionK.id[F], _)
      )
  }
}
