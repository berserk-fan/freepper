package ua.pomo.catalog

import cats.{Eq, Monoid, Show}
import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.{Currency, Money, USD}
import ua.pomo.catalog.optics.uuid
import scala.util.parsing.combinator.Parsers

package object domain extends OrphanInstances {
  @derive(eqv, show)
  @newtype
  case class ReadableId private (value: String)
  object ReadableId {
    private val regex = "^([a-zA-Z0-9\\-]+)$".r
    def parse(s: String): Either[String, ReadableId] = s match {
      case regex(res) => Right(ReadableId(res))
      case _ => Left("Can't create readableId")
    }
  }
}

// instances for types we don't control
trait OrphanInstances {
  implicit val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money                       = USD(0)
      def combine(x: Money, y: Money): Money = x + y
    }

  implicit val currencyEq: Eq[Currency] = Eq.and(Eq.and(Eq.by(_.code), Eq.by(_.symbol)), Eq.by(_.name))

  implicit val moneyEq: Eq[Money] = Eq.and(Eq.by(_.amount), Eq.by(_.currency))

  implicit val moneyShow: Show[Money] = Show.fromToString
}
