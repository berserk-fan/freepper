package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(eqv, show)
sealed trait PageToken
object PageToken {
  @derive(eqv, show)
  case object Empty extends PageToken
  @derive(eqv, show, encoder, decoder)
  case class NonEmpty(size: Long, offset: Long) extends PageToken
}
