package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

import java.util.UUID

class InMemoryModelRepositoryImpl[F[_]: Sync] private[persistance] (ref: Ref[F, Map[ModelUUID, Model]])
    extends ModelRepository[F] {

  override def create(req: CreateModel): F[ModelUUID] = ref.modify { map =>
    val model = Model(
      ModelUUID(UUID.randomUUID()),
      req.readableId,
      req.categoryId,
      req.displayName,
      req.description,
      ModelMinimalPrice(Money(0, USD)),
      ImageList(req.imageListId, ImageListDisplayName(""), List())
    )
    (map + ((model.id, model)), model.id)
  }

  override def get(id: ModelUUID): F[Model] = find(id).flatMap(
    _.fold(new Exception(s"model with id $id not found").raiseError[F, Model])(_.pure[F])
  )

  override def find(id: ModelUUID): F[Option[Model]] = ref.get.map(_.get(id))

  override def findAll(req: FindModel): F[List[Model]] = {
    ref.get
      .map(
        _.values.toList
          .filter(_.categoryId == req.categoryUUID)
          .slice(req.page.offset.toInt, req.page.offset.toInt + req.page.size.toInt))
  }

  override def delete(id: ModelUUID): F[Unit] = ref.update { map =>
    map.get(id).fold(map)(map - _.id)
  }

  override def update(req: UpdateModel): F[Int] = ref.modify { map =>
    map.get(req.id) match {
      case None => (map, 0)
      case Some(model) =>
        var value = model.copy()
        req.imageListId.foreach(x => value = value.copy(imageList = value.imageList.copy(id = x)))
        req.description.foreach(x => value = value.copy(description = x))
        req.displayName.foreach(x => value = value.copy(displayName = x))
        req.categoryId.foreach(x => value = value.copy(categoryId = x))
        req.readableId.foreach(x => value = value.copy(readableId = x))
        (map + ((req.id, value)), 1)
    }
  }
}
