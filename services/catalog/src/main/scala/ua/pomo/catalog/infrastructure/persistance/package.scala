package ua.pomo.catalog.infrastructure

import cats.Show
import cats.data.NonEmptyList
import cats.effect.MonadCancel
import cats.implicits.{toBifunctorOps, toShow, toTraverseOps}
import doobie._
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor, Json, parser}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops.toCoercibleIdOps
import monocle.AppliedLens
import org.postgresql.util.PGobject
import shapeless.Poly1
import ua.pomo.catalog.domain.product.Product

package object persistance {
  implicit def newTypePut[B, A](implicit ev: Coercible[B, A], evp: Put[A]): Put[B] = evp.contramap[B](ev(_))
  implicit def newTypeRead[N: Coercible[R, *], R: Read]: Read[N] = Read[R].map(_.coerce[N])
  implicit val jsonGet: Get[Json] = {
    implicit val showPGobject: Show[PGobject] = Show.show(_.getValue.take(250))

    Get.Advanced.other[PGobject](NonEmptyList.of("json")).temap[Json] { o =>
      parser.parse(o.getValue).leftMap(_.show)
    }
  }

  def jsonAggListJson[T: Decoder]: Get[List[T]] = {
    Get[Json].temap {
      _.asArray
        .map(_.toList)
        .toRight("json is not an array")
        .flatMap {
          _.traverse(Decoder[T].decodeJson).leftMap(_.show)
        }
    }
  }

  def withoutTransaction[A](p: ConnectionIO[A]): ConnectionIO[A] =
    MonadCancel[ConnectionIO].bracket(FC.setAutoCommit(true))(_ => p)(_ => FC.setAutoCommit(false))
}
