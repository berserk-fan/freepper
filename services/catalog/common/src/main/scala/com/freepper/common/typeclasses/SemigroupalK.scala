package com.freepper.common.typeclasses

import cats.*
import cats.arrow.FunctionK
import cats.data.*
import cats.kernel.CommutativeMonoid
import cats.syntax.all.*

import scala.annotation.implicitNotFound
import cats.data.Tuple2K

trait SemigroupalK[Alg[_[_]]] extends Serializable {
  def productK[F[_], G[_]](af: Alg[F], ag: Alg[G]): Alg[Tuple2K[F, G, *]]
}
