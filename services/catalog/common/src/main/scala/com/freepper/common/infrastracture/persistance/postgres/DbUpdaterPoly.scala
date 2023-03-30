//package com.freepper.common.infrastracture.persistance.postgres
//
//import com.freepper.common.domain.auth.AuthConfig
//import doobie.implicits.toSqlInterpolator
//import doobie.{Fragment, Write}
//import shapeless.{Poly0, Poly1}
//import shapeless.*
//
//trait DbUpdaterPoly extends Poly1 {
//  type Res[T] = Case.Aux[Option[T], Option[Fragment]]
//  private def toOpt[T](f: Case.Aux[T, Fragment]): Case.Aux[Option[T], Option[Fragment]] = at(_.map(t => f.apply(t)))
//  def gen[T: Write](fieldName: String): Res[T] = toOpt(at(t => fr"${Fragment.const0(fieldName)}=$t"))
//}
