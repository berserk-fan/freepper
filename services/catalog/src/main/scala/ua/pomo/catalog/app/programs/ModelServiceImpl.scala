package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.infrastructure.persistance.ModelRepositoryImpl

private class ModelServiceImpl[F[_]: MonadCancelThrow, G[_]: Sync] private (xa: FunctionK[G, F],
                                                                            repository: ModelRepository[G])
    extends ModelService[F] {
  def create(model: CreateModel): F[Model] = repository.create(model).flatMap(repository.get).toF
  def delete(id: ModelId): F[Unit] = repository.delete(id).toF
  def findAll(req: FindModel): F[FindModelResponse] =
    repository
      .findAll(req)
      .map { models =>
        val nextPage = if (models.length != req.page.size) {
          PageToken.Empty
        } else {
          PageToken.NonEmpty(req.page.size, req.page.size + req.page.offset)
        }
        FindModelResponse(models, nextPage)
      }
      .toF

  def get(id: ModelId): F[Model] =
    repository
      .find(id)
      .flatMap {
        case Some(value) => value.pure[G]
        case None        => NotFound("model", id).raiseError[G, Model]
      }
      .toF

  def update(req: UpdateModel): F[Model] =
    repository
      .update(req)
      .flatMap { updated =>
        if (updated == 0) {
          new Exception(s"model not found ${req.id}").raiseError[G, Model]
        } else {
          repository.get(req.id)
        }
      }
      .toF

  implicit class TransactOps[T](t: G[T]) {
    def toF: F[T] = xa.apply(t)
  }
}

object ModelServiceImpl {
  def makeInMemory[F[_]: Sync]: F[ModelService[F]] =
    ModelRepositoryImpl
      .makeInMemory[F]
      .map(
        new ModelServiceImpl[F, F](FunctionK.id[F], _)
      )
}
