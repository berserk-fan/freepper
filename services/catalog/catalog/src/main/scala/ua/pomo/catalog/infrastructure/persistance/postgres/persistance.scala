package ua.pomo.catalog.infrastructure.persistance

import doobie.implicits.toSqlInterpolator
import cats.Show
import cats.data.NonEmptyList
import cats.implicits.{toBifunctorOps, toShow, toTraverseOps}
import doobie.{Fragment, Get, Put, Read}
import io.circe.{Decoder, Json, parser}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops.toCoercibleIdOps
import org.postgresql.util.PGobject
import ua.pomo.common.domain.repository.PageToken

package object postgres {
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

  def compileToken(token: PageToken.NonEmpty): Fragment = {
    fr"limit ${token.size} offset ${token.offset}"
  }
}
