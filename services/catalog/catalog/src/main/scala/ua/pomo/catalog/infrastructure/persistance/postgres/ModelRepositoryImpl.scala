package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.implicits.toFunctorOps
import doobie._
import ua.pomo.catalog.domain.model
import ua.pomo.catalog.domain.model._
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

class ModelRepositoryImpl private () extends AbstractPostgresRepository[model.Crud.type](ModelQueries) {
  override def idSelector: ModelId => ModelSelector = (id: ModelId) => ModelSelector.IdIs(id)
}

object ModelRepositoryImpl {
  def apply(): ModelRepository[ConnectionIO] = new ModelRepositoryImpl()

  def makeInMemory[F[_]: Sync]: F[ModelRepository[F]] = {
    Ref[F]
      .of(Map[ModelId, Model]())
      .map(
        new ModelInMemoryRepositoryImpl[F](_)
      )
  }
}
