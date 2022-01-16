package ua.pomo.catalog.programs

import cats.effect.MonadCancelThrow
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps}
import doobie.{ConnectionIO, Transactor}
import doobie.implicits._
import ua.pomo.catalog.domain.category._

object CategoryServiceImpl {
  def apply[F[_]: MonadCancelThrow](
      xa: Transactor[F],
      categoryRepository: CategoryRepository[ConnectionIO]
  ): CategoryService[F] = {
    new CategoryServiceImpl[F](xa, categoryRepository)
  }

  private class CategoryServiceImpl[F[_]: MonadCancelThrow](
      private val xa: Transactor[F],
      private val repository: CategoryRepository[ConnectionIO]
  ) extends CategoryService[F] {
    override def getCategory(id: CategoryId): F[Category] = {
      repository
        .find(id)
        .transact(xa)
        .flatMap { _.fold(new RuntimeException("category not found").raiseError[F,Category])(_.pure[F]) }
    }

    override def findAll(): F[List[Category]] = {
      repository.findAll().transact(xa)
    }

    override def updateCategory(req: UpdateCategory): F[Category] = {
      repository
        .update(req)
        .flatMap { count =>
          val res: ConnectionIO[Category] = if (count > 0) {
            repository.find(req.id).flatMap {
              case Some(updatedCategory) => updatedCategory.pure[ConnectionIO]
              case None =>
                new IllegalStateException("no category after non zero update").raiseError[ConnectionIO, Category]
            }
          } else {
            new RuntimeException("category not found").raiseError[ConnectionIO, Category]
          }

          res
        }
        .transact(xa)
    }

    override def deleteCategory(id: CategoryId): F[Unit] = {
      repository.delete(id).transact(xa)
    }

    override def createCategory(category: Category): F[Category] = {
      repository.create(category)
        .flatMap { id => repository.get(Left(id)) }
        .transact(xa)
    }
  }
}
