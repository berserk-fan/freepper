package com.freepper.common.infrastracture.persistance

trait ValueK[F[_[_]]] {
  type Select
  def apply[U[_]](v: F[U]): U[Select]
}

sealed trait GenericSelector[F[_[_]]]

object GenericSelector {
  case class FieldEquals[F[_[_]]](t: ValueK[F]) extends GenericSelector[F]
  case class All[F[_[_]]]() extends GenericSelector[F]
}
