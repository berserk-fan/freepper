package ua.pomo.catalog.domain

import cats.instances.uuid
import derevo.cats.{ eqv, show }
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image.ImageData

import java.util.UUID

object category {
  @derive(eqv, show)
  @newtype
  case class CategoryName(value: String)

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
  case class Category(
      name: CategoryName,
      id: CategoryId,
      displayName: CategoryDisplayName,
      description: CategoryDescription,
      image: ImageData
  )
}
