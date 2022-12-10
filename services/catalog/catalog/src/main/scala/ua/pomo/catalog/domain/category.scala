package ua.pomo.catalog.domain

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.common.domain.repository
import ua.pomo.common.domain.repository.{Crud, CrudOps, EntityDisplayName, PageToken, Query, Repository}

import java.util.UUID

object category {

  @derive(eqv, show)
  @newtype
  case class CategoryReadableId(value: String)

  @derive(eqv, show)
  @newtype
  case class CategoryUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class CategoryDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class CategoryDescription(value: String)

  @derive(eqv, show)
  case class Category(
      id: CategoryUUID,
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )

  @derive(eqv, show)
  case class CreateCategory(
      id: Option[CategoryUUID],
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )

  @derive(eqv, show)
  case class UpdateCategory(
      id: CategoryUUID,
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
    case class UidIs(uid: CategoryUUID) extends CategorySelector
    case object All extends CategorySelector
  }

  type CategoryRepository[F[_]] = Repository[F, CategoryCrud]

  trait CategoryService[F[_]] {
    def create(category: CreateCategory): F[Category]

    def get(id: CategoryUUID): F[Category]

    def query(req: CategoryQuery): F[QueryCategoriesResponse]

    def update(req: UpdateCategory): F[Category]

    def delete(id: CategoryUUID): F[Unit]
  }

  type CategoryCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateCategory
    override type Update = UpdateCategory
    override type Entity = Category
    override type EntityId = CategoryUUID
    override type Selector = CategorySelector
    override implicit val ops: repository.CrudOps[CategoryCrud] = new CrudOps[CategoryCrud] {
      override def getIdUpdate(update: UpdateCategory): CategoryUUID = update.id

      override def getIdEntity(entity: Category): CategoryUUID = entity.id

      override def entityDisplayName: EntityDisplayName = EntityDisplayName("category")
    }
  }
}
