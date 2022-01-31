package ua.pomo.catalog.infrastructure.persistance

import doobie.implicits._
import doobie.{Fragment, Write}
import shapeless._

trait DbUpdaterPoly extends Poly1 {
  type Res[T] = Case.Aux[Option[T], Option[Fragment]]
  def toOpt[T](f: Case.Aux[T, Fragment]): Case.Aux[Option[T], Option[Fragment]] = at(_.map(t => f.apply(t)))
  def gen[T: Write](fieldName: String): Res[T] = toOpt(at(t => fr"${Fragment.const0(fieldName)}=$t"))
}
