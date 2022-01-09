package ua.pomo.catalog.domain

import ua.pomo.catalog.optics.uuid
import derevo.cats.{ eqv, show }
import derevo.circe.magnolia.{ keyDecoder, keyEncoder }
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money

import java.util.UUID

object product {
  @derive(keyDecoder, keyEncoder, eqv, show, uuid)
  @newtype
  case class ProductId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ProductName(value: String)

  @derive(eqv, show)
  @newtype
  case class ProductDescription(value: String)

  @derive(eqv, show)
  case class Product(
      id: ProductId,
      name: ProductName,
      description: ProductDescription,
      price: Money
  )
}
