package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

import java.util.UUID
import shapeless._
import monocle.syntax.all._
import ua.pomo.catalog.domain.category.{CategoryReadableId, CategoryUUID}
import ua.pomo.catalog.domain.parameter.{ParamListDisplayName, ParameterList}

class InMemoryModelRepositoryImpl[F[_]: Sync] private[persistance] (ref: Ref[F, Map[ModelId, Model]])
    extends ModelRepository[F] {

  override def create(req: CreateModel): F[ModelId] = ref.modify { map =>
    val model = Model(
      ModelId(UUID.randomUUID()),
      req.readableId,
      req.categoryId,
      CategoryReadableId("some-category"),
      req.displayName,
      req.description,
      ModelMinimalPrice(Money(0, USD)),
      req.parameterListIds.map(id => ParameterList(id, ParamListDisplayName(""), List())),
      ImageList(req.imageListId, ImageListDisplayName(""), List())
    )
    (map + ((model.id, model)), model.id)
  }

  override def get(id: ModelId): F[Model] = find(id).flatMap(
    _.fold(new Exception(s"model with id $id not found").raiseError[F, Model])(_.pure[F])
  )

  override def find(id: ModelId): F[Option[Model]] = ref.get.map(_.get(id))

  override def findAll(req: ModelQuery): F[List[Model]] = {
    val filter: Model => Boolean = req.selector match {
      case ModelSelector.All =>
        _ => true
      case ModelSelector.IdIs(id)         => _.id == id
      case ModelSelector.CategoryIdIs(id) => _.categoryUid == id
    }
    ref.get
      .map(
        _.values.toList
          .filter(filter)
          .slice(req.page.offset.toInt, req.page.offset.toInt + req.page.size.toInt)
      )
  }

  override def delete(id: ModelId): F[Int] = ref.modify { map =>
    map.get(id).fold((map, 0))(x => (map - x.id, 1))
  }

  override def update(command: UpdateModel): F[Int] = ref.modify { map =>
    object updateObj extends InMemoryUpdaterPoly[Model] {
      implicit val readableId: Res[ModelReadableId] = gen(_.focus(_.readableId))
      implicit val categoryId: Res[CategoryUUID] = gen(_.focus(_.categoryUid))
      implicit val displayName: Res[ModelDisplayName] = gen(_.focus(_.displayName))
      implicit val description: Res[ModelDescription] = gen(_.focus(_.description))
      implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
    }
    val updater = Generic[UpdateModel].to(command).drop(Nat._1).map(updateObj).toList.flatten.reduce(_ andThen _)
    map.get(command.id).fold((map, 0))(x => (map + (command.id -> updater(x)), 1))
  }
}
