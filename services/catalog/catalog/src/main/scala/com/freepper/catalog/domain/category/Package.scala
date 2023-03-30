package com.freepper.catalog.domain.category

import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, Repository}
import io.circe.Decoder

import java.util.UUID

case class CategoryReadableId(value: String)

case class CategoryId(value: UUID)

case class CategoryDisplayName(value: String)

case class CategoryDescription(value: String)

case class Category(
    id: CategoryId,
    readableId: CategoryReadableId,
    displayName: CategoryDisplayName,
    description: CategoryDescription
) derives Decoder

case class CreateCategory(
    id: CategoryId,
    readableId: CategoryReadableId,
    displayName: CategoryDisplayName,
    description: CategoryDescription
)

case class UpdateCategory(
    id: CategoryId,
    readableId: Option[CategoryReadableId],
    displayName: Option[CategoryDisplayName],
    description: Option[CategoryDescription]
)

type CategoryQuery = crud.Query[CategorySelector]
sealed trait CategorySelector
object CategorySelector {
  case class RidIs(rid: CategoryReadableId) extends CategorySelector
  case class UidIs(uid: CategoryId) extends CategorySelector
  case object All extends CategorySelector
}

type CategoryRepository[F[_]] = Repository[F, CategoryCrud]

import Crud.*
type CategoryCrud[X] = X match {
  case Create   => CreateCategory
  case Update   => UpdateCategory
  case Entity   => Category
  case EntityId => CategoryId
  case Query    => CategoryQuery
  case CrudName => "category"
}
