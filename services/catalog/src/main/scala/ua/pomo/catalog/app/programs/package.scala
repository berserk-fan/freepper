package ua.pomo.catalog.app

import cats.~>

package object programs {
  implicit class MapKOps[F[_], G[_], T](value: F[T]) {
    def mapK(f: F ~> G): G[T] = f(value)
  }
}
