package ua.pomo.catalog.domain

import derevo.cats._
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.category.{CategoryId, CategoryReadableId}
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListId}
import ua.pomo.catalog.domain.parameter.{ParameterList, ParameterListId}
import ua.pomo.common.domain.crud._

import java.util.UUID

object model {
  @derive(eqv, show, decoder)
  @newtype
  case class ModelId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ModelReadableId(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ModelDisplayName(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ModelDescription(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ModelImageList(value: ImageList)

  @derive(eqv, show, decoder)
  @newtype
  case class ModelMinimalPrice(value: Money)

  @derive(eqv, show, decoder)
  case class Model(
      id: ModelId,
      readableId: ModelReadableId,
      categoryUid: CategoryId,
      categoryRid: CategoryReadableId,
      displayName: ModelDisplayName,
      description: ModelDescription,
      minimalPrice: ModelMinimalPrice,
      parameterLists: List[ParameterList],
      imageList: ImageList
  )

  @derive(eqv, show)
  case class CreateModel(
      id: Option[ModelId],
      readableId: ModelReadableId,
      categoryId: CategoryId,
      displayName: ModelDisplayName,
      description: ModelDescription,
      imageListId: ImageListId,
      parameterListIds: List[ParameterListId]
  )

  @derive(eqv, show)
  case class UpdateModel(
      id: ModelId,
      readableId: Option[ModelReadableId],
      categoryId: Option[CategoryId],
      displayName: Option[ModelDisplayName],
      description: Option[ModelDescription],
      imageListId: Option[ImageListId]
  )

  type ModelQuery = Query[ModelSelector]

  @derive(eqv, show)
  case class FindModelResponse(models: List[Model], nextPageToken: PageToken)

  @derive(eqv, show)
  sealed trait ModelSelector
  object ModelSelector {
    case object All extends ModelSelector
    case class IdIs(id: ModelId) extends ModelSelector
    case class CategoryIdIs(id: CategoryId) extends ModelSelector
  }

  type ModelRepository[F[_]] = Repository[F, Crud.type]

  trait ModelService[F[_]] {
    def create(category: CreateModel): F[Model]

    def get(id: ModelId): F[Model]

    def findAll(req: ModelQuery): F[FindModelResponse]

    def update(req: UpdateModel): F[Model]

    def delete(id: ModelId): F[Unit]
  }

  type ModelCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateModel
    override type Update = UpdateModel
    override type Entity = Model
    override type EntityId = ModelId
    override type Selector = ModelSelector
    implicit val ops: RepoOps[ModelCrud] = new RepoOps[ModelCrud] {
      override def getIdUpdate(update: UpdateModel): ModelId = update.id

      override def getIdEntity(entity: Model): ModelId = entity.id

      override def entityDisplayName: EntityDisplayName = Entity.Model.name

      override def getIdCreate(update: CreateModel): Option[ModelId] = update.id
    }
  }
}
