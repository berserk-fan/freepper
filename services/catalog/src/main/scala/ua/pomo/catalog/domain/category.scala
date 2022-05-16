package ua.pomo.catalog.domain

import cats.Show
import cats.implicits.toShow
import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

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
  case class Category(id: CategoryUUID,
                      readableId: CategoryReadableId,
                      displayName: CategoryDisplayName,
                      description: CategoryDescription)

  @derive(eqv, show)
  case class CreateCategory(
      readableId: CategoryReadableId,
      displayName: CategoryDisplayName,
      description: CategoryDescription
  )

  @derive(eqv, show)
  case class UpdateCategory(id: CategoryUUID,
                            readableId: Option[CategoryReadableId],
                            displayName: Option[CategoryDisplayName],
                            description: Option[CategoryDescription])

  @derive(eqv, show)
  case class QueryCategoriesResponse(categories: List[Category], nextToken: PageToken)

  sealed trait CategorySelector
  object CategorySelector {
    case class RidIs(rid: CategoryReadableId) extends CategorySelector
    case class UidIs(uid: CategoryUUID) extends CategorySelector
    case object All extends CategorySelector
  }

  case class CategoryQuery(selector: CategorySelector, token: PageToken.NonEmpty)

  trait CategoryRepository[F[_]] {
    def create(category: CreateCategory): F[CategoryUUID]

    def get(id: CategoryUUID): F[Category]

    def find(id: CategoryUUID): F[Option[Category]]

    def query(req: CategoryQuery): F[List[Category]]

    def update(req: UpdateCategory): F[Int]

    def delete(id: CategoryUUID): F[Unit]
  }

  trait CategoryService[F[_]] {
    def create(category: CreateCategory): F[Category]

    def get(id: CategoryUUID): F[Category]

    def query(req: CategoryQuery): F[QueryCategoriesResponse]

    def update(req: UpdateCategory): F[Category]

    def delete(id: CategoryUUID): F[Unit]
  }
}
