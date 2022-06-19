package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.Sync
import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import cats.~>
import doobie.ConnectionIO
import doobie.util.transactor.Transactor
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.infrastructure.persistance.postgres.ModelRepositoryImpl

private class ModelServiceImpl[F[_], G[_]: Sync] private (xa: G ~> F, repository: ModelRepository[G])
    extends ModelService[F] {

  def create(model: CreateModel): F[Model] = repository.create(model).flatMap(repository.get).mapK(xa)
  def delete(id: ModelId): F[Unit] =
    repository
      .delete(id)
      .flatMap { deleted =>
        if (deleted == 0) {
          NotFound("model", id).raiseError[G, Unit]
        } else {
          ().pure[G]
        }
      }
      .mapK(xa)
  def findAll(req: ModelQuery): F[FindModelResponse] =
    repository
      .findAll(req)
      .map(models => FindModelResponse(models, computeNextPageToken(req.page, models)))
      .mapK(xa)

  def get(id: ModelId): F[Model] =
    repository
      .find(id)
      .flatMap {
        case Some(value) => value.pure[G]
        case None        => NotFound("model", id).raiseError[G, Model]
      }
      .mapK(xa)

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
      .mapK(xa)
}

object ModelServiceImpl {
  def apply[F[_]: Async](transactor: Transactor[F], repository: ModelRepository[ConnectionIO]): ModelService[F] = {
    new ModelServiceImpl[F, ConnectionIO](transactor.trans, repository)
  }
  def makeInMemory[F[_]: Sync]: F[ModelService[F]] =
    ModelRepositoryImpl
      .makeInMemory[F]
      .map(
        new ModelServiceImpl[F, F](FunctionK.id[F], _)
      )
}
