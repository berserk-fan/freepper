package ua.pomo.catalog.domain

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.image.ImageList
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object model {
  @derive(eqv, show, uuid)
  @newtype
  case class ModelUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ModelReadableId(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelDescription(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelImageList(value: ImageList)

  @derive(eqv, show)
  @newtype
  case class ModelMinimalPrice(value: Money)

  @derive(eqv, show)
  case class Model(
      id: ModelUUID,
      readableId: ModelReadableId,
      description: ModelDescription,
      imageList: ImageList,
      minimalPrice: ModelMinimalPrice
  )
}
