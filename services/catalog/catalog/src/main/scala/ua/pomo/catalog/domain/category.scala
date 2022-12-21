package ua.pomo.catalog.domain

import derevo.cats._
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.common.domain.crud
import ua.pomo.common.domain.crud.{Crud, EntityDisplayName, PageToken, Query, RepoOps, Repository}

import java.util.UUID

object category {

  @derive(eqv, show, decoder)
  @newtype
  case class CategoryReadableId(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class CategoryId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class CategoryDisplayName(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class CategoryDescription(value: String)

  @derive(eqv, show, decoder)
  case class Category(
      id: CategoryId,
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )

  @derive(eqv, show)
  case class CreateCategory(
      id: Option[CategoryId],
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )

  @derive(eqv, show)
  case class UpdateCategory(
      id: CategoryId,
      readableId: Option[CategoryReadableId],
      displayName: Option[CategoryDisplayName],
      description: Option[CategoryDescription]
  )

  @derive(eqv, show)
  case class QueryCategoriesResponse(categories: List[Category], nextToken: PageToken)

  type CategoryQuery = Query[CategorySelector]
  sealed trait CategorySelector
  object CategorySelector {
    case class RidIs(rid: CategoryReadableId) extends CategorySelector
    case class UidIs(uid: CategoryId) extends CategorySelector
    case object All extends CategorySelector
  }

  type CategoryRepository[F[_]] = Repository[F, CategoryCrud]

  trait CategoryService[F[_]] {
    def create(category: CreateCategory): F[Category]

    def get(id: CategoryId): F[Category]

    def query(req: CategoryQuery): F[QueryCategoriesResponse]

    def update(req: UpdateCategory): F[Category]

    def delete(id: CategoryId): F[Unit]
  }

  type CategoryCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateCategory
    override type Update = UpdateCategory
    override type Entity = Category
    override type EntityId = CategoryId
    override type Selector = CategorySelector
    implicit val ops: crud.RepoOps[CategoryCrud] = new RepoOps[CategoryCrud] {
      override def getIdUpdate(update: UpdateCategory): CategoryId = update.id

      override def getIdEntity(entity: Category): CategoryId = entity.id

      override def entityDisplayName: EntityDisplayName = Entity.Category.name

      override def getIdCreate(update: CreateCategory): Option[CategoryId] = update.id
    }
  }
}
