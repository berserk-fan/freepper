package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.infrastructure.persistance.ModelRepositoryImpl

private class ModelServiceImpl[F[_]: MonadCancelThrow, G[_]: Sync] private (xa: FunctionK[G, F],
                                                                            repository: ModelRepository[G])
    extends ModelService[F] {
  def create(model: CreateModel): F[Model] = repository.create(model).flatMap(repository.get).toF
  def delete(id: ModelUUID): F[Unit] = repository.delete(id).toF
  def find(id: ModelUUID): F[Option[Model]] = repository.find(id).toF
  def findAll(req: FindModel): F[FindModelResponse] =
    repository
      .findAll(req)
      .map { models =>
        val nextPage = if (models.length != req.page.size) {
          PageToken.Empty
        } else {
          PageToken.NotEmpty(req.page.size, req.page.size + req.page.offset)
        }
        FindModelResponse(models, nextPage)
      }
      .toF

  def get(id: ModelUUID): F[Model] = repository.get(id).toF
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
