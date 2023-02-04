package ua.pomo.catalog.infrastructure.persistance

import cats.Show
import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits.{toBifunctorOps, toShow, toTraverseOps}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment, Get, Put, Read}
import io.circe.{Decoder, Json, parser}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops.toCoercibleIdOps
import org.postgresql.util.PGobject
import ua.pomo.catalog.domain.RegistryHelper
import ua.pomo.common.domain.registry.Registry
import ua.pomo.common.domain.crud.Crud
import ua.pomo.common.domain.crud.{PageToken, Repository}

package object postgres {
  implicit def newTypePut[B, A](implicit ev: Coercible[B, A], evp: Put[A]): Put[B] = evp.contramap[B](ev(_))
  implicit def newTypeRead[N: Coercible[R, *], R: Read]: Read[N] = Read[R].map(_.coerce[N])
  val jsonGet: Get[Json] = {
    implicit val showPGobject: Show[PGobject] = Show.show(_.getValue.take(250))

    Get.Advanced.other[PGobject](NonEmptyList.of("json")).temap[Json] { o =>
      parser.parse(o.getValue).leftMap(_.show)
    }
  }

  def readJsonFromView[T: Decoder]: Get[T] = {
    jsonGet.temap(_.as[T].left.map(_.getMessage()))
  }

  def jsonAggListJson[T: Decoder]: Get[List[T]] = {
    jsonGet.temap {
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

  def postgresRepoRegistry: Registry[Lambda[`T <: Crud` => Repository[ConnectionIO, T]]] =
    RegistryHelper.createRegistry[Lambda[`T <: Crud` => Repository[ConnectionIO, T]]](
      CategoryRepository.postgres,
      ImageRepository.postgres,
      ImageListRepository.postgres,
      ModelRepository.postgres,
      ProductRepository.postgres,
      ParameterListRepository.postgres
    )

  def inMemoryRepoRegistry[F[_]: Sync]: F[Registry[Lambda[`T <: Crud` => Repository[F, T]]]] = for {
    cr <- CategoryRepository.inmemory[F]
    ir <- ImageRepository.inmemory[F]
    ilr <- ImageListRepository.inmemory[F]
    mr <- ModelRepository.inmemory[F]
    pr <- ProductRepository.inmemory[F]
    plr <- ParameterListRepository.inmemory[F]
  } yield RegistryHelper.createRegistry[Lambda[`T <: Crud` => Repository[F, T]]](cr, ir, ilr, mr, pr, plr)
}
