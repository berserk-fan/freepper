package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(eqv, show, encoder, decoder)
sealed trait PageToken
object PageToken {
  @derive(eqv, show, encoder, decoder)
  case object Empty extends PageToken
  @derive(eqv, show, encoder, decoder)
  case class NotEmpty(size: Long, offset: Long) extends PageToken
}
