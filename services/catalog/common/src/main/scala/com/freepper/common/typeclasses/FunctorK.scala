package com.freepper.common.typeclasses

import cats.~>
trait FunctorK[Alg[_[_]]] {
  def mapK[F[_], G[_]](af: Alg[F])(fk: F ~> G): Alg[G]
}
