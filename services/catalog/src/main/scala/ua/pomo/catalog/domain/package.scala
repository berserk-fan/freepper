package ua.pomo.catalog

import cats.{Eq, Monoid, Show}
import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.{Currency, Money, USD}

package object domain extends OrphanInstances

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
