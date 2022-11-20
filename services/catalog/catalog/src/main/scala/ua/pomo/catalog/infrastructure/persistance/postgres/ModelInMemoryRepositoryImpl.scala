package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import monocle.syntax.all._
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category.{CategoryReadableId, CategoryUUID}
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter.{ParamListDisplayName, ParameterList}
import ua.pomo.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}

import java.util.UUID
import shapeless._
import ua.pomo.catalog.domain.model

case class ModelInMemoryRepositoryImpl[F[_]: Sync] private[persistance] (ref: Ref[F, Map[ModelId, Model]])
    extends AbstractInMemoryRepository[F, model.Crud.type](ref) {
  override def creator: CreateModel => Model = (req: CreateModel) =>
    Model(
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

  override def filter: ModelSelector => Model => Boolean = {
    case ModelSelector.All =>
      _ => true
    case ModelSelector.IdIs(id)         => _.id == id
    case ModelSelector.CategoryIdIs(id) => _.categoryUid == id
  }

  object updateObj extends InMemoryUpdaterPoly[Model] {
    implicit val readableId: Res[ModelReadableId] = gen(_.focus(_.readableId))
    implicit val categoryId: Res[CategoryUUID] = gen(_.focus(_.categoryUid))
    implicit val displayName: Res[ModelDisplayName] = gen(_.focus(_.displayName))
    implicit val description: Res[ModelDescription] = gen(_.focus(_.description))
    implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
  }

  override def update(req: UpdateModel): F[Int] = {
    updateHelper(req, updateObj, Generic[UpdateModel])
  }
}
