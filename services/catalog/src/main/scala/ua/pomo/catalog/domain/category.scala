package ua.pomo.catalog.domain

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object category {
  type CategoryId = CategoryUUID Either CategoryReadableId

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
  case class Category(id: CategoryUUID,
                      readableId: CategoryReadableId,
                      displayName: CategoryDisplayName,
                      description: CategoryDescription)

  @derive(eqv, show)
  case class UpdateCategory(id: CategoryId,
                            readableId: Option[CategoryReadableId],
                            displayName: Option[CategoryDisplayName],
                            description: Option[CategoryDescription])

  trait CategoryRepository[F[_]] {
    def createCategory(category: Category): F[Category]

    def findCategory(id: CategoryId): F[Option[Category]]

    def findAll(): F[List[Category]]

    def updateCategory(req: UpdateCategory): F[Int]

    def deleteCategory(id: CategoryId): F[Unit]
  }

  trait CategoryService[F[_]] {
    def createCategory(category: Category): F[Category]

    def getCategory(id: CategoryId): F[Category]

    def findAll(): F[List[Category]]

    def updateCategory(req: UpdateCategory): F[Category]

    def deleteCategory(id: CategoryId): F[Unit]
  }
}
