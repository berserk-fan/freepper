package ua.pomo.catalog.domain

import cats.{Eq, Show}
import cats.implicits._
import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object category {
  @derive(eqv, show)
  @newtype
  case class CategoryReadableId(value: String)

  @derive(eqv, show, uuid)
  @newtype
  case class CategoryId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class CategoryDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class CategoryDescription(value: String)

  @derive(eqv, show)
  case class Category(id: CategoryId,
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
  case class UpdateCategory(id: CategoryId,
                            readableId: Option[CategoryReadableId],
                            displayName: Option[CategoryDisplayName],
                            description: Option[CategoryDescription])

  @derive(eqv, show)
  case class QueryCategoriesResponse(categories: List[Category], nextToken: PageToken)

  sealed trait CategorySelector
  object CategorySelector {
    case class IdIs(categoryId: CategoryId) extends CategorySelector
    case object All extends CategorySelector
  }

  case class CategoryQuery(selector: CategorySelector, token: PageToken.NonEmpty)

  trait CategoryRepository[F[_]] {
    def create(category: CreateCategory): F[CategoryId]

    def get(id: CategoryId): F[Category]

    def find(id: CategoryId): F[Option[Category]]

    def query(req: CategoryQuery): F[List[Category]]

    def update(req: UpdateCategory): F[Int]

    def delete(id: CategoryId): F[Unit]
  }

  trait CategoryService[F[_]] {
    def create(category: CreateCategory): F[Category]

    def get(id: CategoryId): F[Category]

    def query(req: CategoryQuery): F[QueryCategoriesResponse]

    def update(req: UpdateCategory): F[Category]

    def delete(id: CategoryId): F[Unit]
  }
}
