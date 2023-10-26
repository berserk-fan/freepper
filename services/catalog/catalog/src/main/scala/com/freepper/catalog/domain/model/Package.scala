package com.freepper.catalog.domain.model

import squants.market.Money
import com.freepper.catalog.domain.category.{CategoryId, CategoryReadableId}
import com.freepper.catalog.domain.imageList.{ImageList, ImageListId}
import com.freepper.catalog.domain.parameter.{ParameterList, ParameterListId}
import com.freepper.common.domain.crud.{Crud, Repository}
import com.freepper.common.domain.crud

import java.util.UUID

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

sealed trait ModelSelector
object ModelSelector {
  case object All extends ModelSelector
  case class IdIs(id: ModelId) extends ModelSelector
  case class CategoryIdIs(id: CategoryId) extends ModelSelector
  case class RidIs(id: ModelReadableId) extends ModelSelector
}

type ModelRepository[F[_]] = Repository[F, ModelCrud]
type ModelQuery = crud.Query[ModelSelector]

import Crud.*
type ModelCrud[X] = X match {
  case Create   => CreateModel
  case Update   => UpdateModel
  case Entity   => Model
  case EntityId => ModelId
  case Query    => ModelQuery
  case CrudName => "model"
}
