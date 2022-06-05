package ua.pomo.catalog.domain

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.category.{CategoryReadableId, CategoryUUID}
import ua.pomo.catalog.domain.image.{ImageList, ImageListId}
import ua.pomo.catalog.domain.parameter.{ParameterList, ParameterListId}
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object model {
  @derive(eqv, show, uuid)
  @newtype
  case class ModelId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ModelReadableId(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelDescription(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelImageList(value: ImageList)

  @derive(eqv, show)
  @newtype
  case class ModelMinimalPrice(value: Money)

  @derive(eqv, show)
  case class Model(
      id: ModelId,
      readableId: ModelReadableId,
      categoryUid: CategoryUUID,
      categoryRid: CategoryReadableId,
      displayName: ModelDisplayName,
      description: ModelDescription,
      minimalPrice: ModelMinimalPrice,
      parameterLists: List[ParameterList],
      imageList: ImageList
  )

  @derive(eqv, show)
  case class CreateModel(
      readableId: ModelReadableId,
      categoryId: CategoryUUID,
      displayName: ModelDisplayName,
      description: ModelDescription,
      imageListId: ImageListId,
      parameterListIds: List[ParameterListId]
  )

  @derive(eqv, show)
  case class UpdateModel(
      id: ModelId,
      readableId: Option[ModelReadableId],
      categoryId: Option[CategoryUUID],
      displayName: Option[ModelDisplayName],
      description: Option[ModelDescription],
      imageListId: Option[ImageListId]
  )

  @derive(eqv, show)
  case class ModelQuery(selector: ModelSelector, page: PageToken.NonEmpty)

  @derive(eqv, show)
  case class FindModelResponse(models: List[Model], nextPageToken: PageToken)

  @derive(eqv, show)
  sealed trait ModelSelector
  object ModelSelector {
    case object All extends ModelSelector
    case class IdIs(id: ModelId) extends ModelSelector
    case class CategoryIdIs(id: CategoryUUID) extends ModelSelector
  }

  trait ModelRepository[F[_]] {
    def create(model: CreateModel): F[ModelId]

    def get(id: ModelId): F[Model]

    def find(id: ModelId): F[Option[Model]]

    def findAll(req: ModelQuery): F[List[Model]]

    def update(req: UpdateModel): F[Int]

    def delete(id: ModelId): F[Int]
  }

  trait ModelService[F[_]] {
    def create(category: CreateModel): F[Model]

    def get(id: ModelId): F[Model]

    def findAll(req: ModelQuery): F[FindModelResponse]

    def update(req: UpdateModel): F[Model]

    def delete(id: ModelId): F[Unit]
  }
}
