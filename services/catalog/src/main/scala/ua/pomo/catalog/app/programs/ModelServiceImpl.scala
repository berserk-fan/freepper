package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{MonadCancelThrow, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.infrastructure.persistance.ModelRepositoryImpl

private class ModelServiceImpl[F[_]: MonadCancelThrow, G[_]: Sync] private (xa: FunctionK[G, F],
                                                                            repository: ModelRepository[G]) extends ModelService[F] {
  def create(model: CreateModel): F[Model] = repository.create(model).flatMap(id => repository.get(ModelId(id))).toF
  def delete(id: ModelId): F[Unit] = repository.delete(id).toF
  def find(id: ModelId): F[Option[Model]] = repository.find(id).toF
  def findAll(req: FindModel): F[List[Model]] = repository.findAll(req).toF
  def get(id: ModelId): F[Model] = repository.get(id).toF
  def update(req: UpdateModel): F[Model] = repository.update(req).flatMap { updated =>
    if(updated == 0) {
      new Exception(s"model not found ${req.id}").raiseError[G, Model]
    } else {
      repository.get(ModelId(req.id))
    }
  }.toF

  implicit class TransactOps[T](t: G[T]) {
    def toF: F[T] = xa.apply(t)
  }
}

object ModelServiceImpl {
  def makeInMemory[F[_]: Sync]: F[ModelService[F]] = ModelRepositoryImpl.makeInMemory[F].map (
    new ModelServiceImpl[F,F](FunctionK.id[F], _)
  )
}
