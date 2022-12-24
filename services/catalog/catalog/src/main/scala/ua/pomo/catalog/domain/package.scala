package ua.pomo.catalog

import cats.{Eq, Monoid, Show}
import io.circe.Decoder
import squants.market.{Currency, Money, USD}

import java.util.UUID

package object domain {
  implicit val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money = USD(0)

      def combine(x: Money, y: Money): Money = x + y
    }

  implicit val currencyEq: Eq[Currency] = Eq.and(Eq.and(Eq.by(_.code), Eq.by(_.symbol)), Eq.by(_.name))

  implicit val moneyEq: Eq[Money] = Eq.and(Eq.by(_.amount), Eq.by(_.currency))

  implicit val moneyShow: Show[Money] = Show.fromToString

  implicit val moneyDecoder: Decoder[Money] = Decoder.instance(_.as[Double].map(x => Money(x, USD)))

  implicit val uuidDecoder: Decoder[UUID] = Decoder.instance(_.as[String].map(UUID.fromString))

}
