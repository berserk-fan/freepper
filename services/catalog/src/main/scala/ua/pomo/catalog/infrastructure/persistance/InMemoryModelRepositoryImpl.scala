package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.model._

import java.util.UUID

class InMemoryModelRepositoryImpl[F[_]: Sync] private [persistance] (ref: Ref[F, Map[ModelUUID, Model]]) extends ModelRepository[F] {
  private implicit class FindById(m: Map[ModelUUID, Model]) {
    def findById(id: ModelId): Option[Model] = id.value match {
      case Left(uuid)        => m.get(uuid)
      case Right(readableId) => m.values.find(_.readableId == readableId)
    }
  }

  override def create(model: Model): F[ModelUUID] = ref.modify { map =>
    val id = ModelUUID(UUID.randomUUID())
    (map + (id -> model.copy(id = id)), id)
  }

  override def get(id: ModelId): F[Model] = find(id).flatMap(
    _.fold(new Exception(s"model with id $id not found").raiseError[F, Model])(_.pure[F])
  )

  override def find(id: ModelId): F[Option[Model]] = ref.get.map(_.findById(id))

  override def findAll(req: FindModel): F[List[Model]] = {
    ref.get
      .map(
        _.values.toList
          .filter(_.categoryId == req.categoryUUID)
          .slice(req.offset.toInt, req.offset.toInt + req.limit.toInt))
  }

  override def delete(id: ModelId): F[Unit] = ref.update { map =>
    map.findById(id).fold(map)(map - _.id)
  }

  override def update(req: UpdateModel): F[Int] = ???
}
