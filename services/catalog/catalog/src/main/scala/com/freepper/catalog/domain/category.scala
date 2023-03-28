package com.freepper.catalog.domain





import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, EntityDisplayName, Query, RepoOps, Repository}

import java.util.UUID

object category {



  case class CategoryReadableId(value: String)



  case class CategoryId(value: UUID)



  case class CategoryDisplayName(value: String)



  case class CategoryDescription(value: String)


  case class Category(
      id: CategoryId,
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )


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

  type CategoryQuery = Query[CategorySelector]
  sealed trait CategorySelector
  object CategorySelector {
    case class RidIs(rid: CategoryReadableId) extends CategorySelector
    case class UidIs(uid: CategoryId) extends CategorySelector
    case object All extends CategorySelector
  }

  type CategoryRepository[F[_]] = Repository[F, CategoryCrud]

  type CategoryCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateCategory
    override type Update = UpdateCategory
    override type Entity = Category
    override type EntityId = CategoryId
    override type Selector = CategorySelector
  }
}
