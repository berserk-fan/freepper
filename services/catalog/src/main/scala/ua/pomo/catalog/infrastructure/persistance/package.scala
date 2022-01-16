package ua.pomo.catalog.infrastructure

import doobie.{ Put, Read }
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops.toCoercibleIdOps

package object persistance {
  implicit def newTypePut[B, A](implicit ev: Coercible[B, A], evp: Put[A]): Put[B] = evp.contramap[B](ev(_))
  implicit def newTypeRead[N: Coercible[R, *], R: Read]: Read[N]                   = Read[R].map(_.coerce[N])
}
