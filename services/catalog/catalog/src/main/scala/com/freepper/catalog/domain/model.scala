package com.freepper.catalog.domain




import squants.market.Money
import com.freepper.catalog.domain.category.{CategoryId, CategoryReadableId}
import com.freepper.catalog.domain.imageList.{ImageList, ImageListId}
import com.freepper.catalog.domain.parameter.{ParameterList, ParameterListId}
import com.freepper.common.domain.crud._

import java.util.UUID

object model {

  case class ModelId(value: UUID)


  case class ModelReadableId(value: String)


  case class ModelDisplayName(value: String)



  case class ModelDescription(value: String)



  case class ModelImageList(value: ImageList)



  case class ModelMinimalPrice(value: Money)


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


  case class CreateModel(
      id: ModelId,
      readableId: ModelReadableId,
      categoryId: CategoryId,
      displayName: ModelDisplayName,
      description: ModelDescription,
      imageListId: ImageListId,
      parameterListIds: List[ParameterListId]
  )


  case class UpdateModel(
      id: ModelId,
      readableId: Option[ModelReadableId],
      categoryId: Option[CategoryId],
      displayName: Option[ModelDisplayName],
      description: Option[ModelDescription],
      imageListId: Option[ImageListId]
  )

  type ModelQuery = Query[ModelSelector]


  sealed trait ModelSelector
  object ModelSelector {
    case object All extends ModelSelector
    case class IdIs(id: ModelId) extends ModelSelector
    case class CategoryIdIs(id: CategoryId) extends ModelSelector
    case class RidIs(id: ModelReadableId) extends ModelSelector
  }

  type ModelRepository[F[_]] = Repository[F, Crud.type]

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

      override def getIdCreate(create: CreateModel): ModelId = create.id
    }
  }
}
