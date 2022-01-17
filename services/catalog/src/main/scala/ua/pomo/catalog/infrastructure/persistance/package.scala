package ua.pomo.catalog.infrastructure

import cats.Show
import cats.data.NonEmptyList
import cats.implicits.{toBifunctorOps, toShow}
import doobie.{Get, Put, Read}
import io.circe.{Json, parser}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops.toCoercibleIdOps
import org.postgresql.util.PGobject

package object persistance {
  implicit def newTypePut[B, A](implicit ev: Coercible[B, A], evp: Put[A]): Put[B] = evp.contramap[B](ev(_))
  implicit def newTypeRead[N: Coercible[R, *], R: Read]: Read[N]                   = Read[R].map(_.coerce[N])
  implicit val jsonGet: Get[Json] = {
    implicit val showPGobject: Show[PGobject] = Show.show(_.getValue.take(250))

    Get.Advanced.other[PGobject](NonEmptyList.of("json")).temap[Json] { o =>
      parser.parse(o.getValue).leftMap(_.show)
    }
  }
}