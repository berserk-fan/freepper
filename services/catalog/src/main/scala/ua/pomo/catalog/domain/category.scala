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
  case class CategoryUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class CategoryDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class CategoryDescription(value: String)

  @derive(eqv, show)
  case class Category(uuid: CategoryUUID,
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

  trait CategoryRepository[F[_]] {
    def create(category: CreateCategory): F[CategoryUUID]

    def get(id: CategoryUUID): F[Category]

    def find(id: CategoryUUID): F[Option[Category]]

    def findAll(): F[List[Category]]

    def update(req: UpdateCategory): F[Int]

    def delete(id: CategoryUUID): F[Unit]
  }

  trait CategoryService[F[_]] {
    def create(category: CreateCategory): F[Category]

    def get(id: CategoryUUID): F[Category]

    def findAll(): F[List[Category]]

    def update(req: UpdateCategory): F[Category]

    def delete(id: CategoryUUID): F[Unit]
  }
}
