package com.freepper.catalog.infrastructure.persistance.postgres

import cats.Show
import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits.{toBifunctorOps, toShow, toTraverseOps}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import com.freepper.catalog.domain.Registry
import com.freepper.common.domain.crud.{PageToken, Repository}
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment, Get, Put, Read}
import io.circe.{Decoder, Json, parser}
import org.postgresql.util.PGobject

export com.freepper.catalog.knowledge.implicits.given

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

def postgresRepoRegistry: Registry[[C[_]] =>> Repository[ConnectionIO, C]] =
  Registry(
    CategoryRepository.postgres,
    ImageRepository.postgres,
    ImageListRepository.postgres,
    ModelRepository.postgres,
    ProductRepository.postgres,
    ParameterListRepository.postgres
  )

def inMemoryRepoRegistry[F[_]: Sync]: F[Registry[[C[_]] =>> Repository[F, C]]] = for {
  ir <- ImageRepository.inmemory[F]
  cr <- CategoryRepository.inmemory[F]
  ilr <- ImageListRepository.inmemory[F]
  mr <- ModelRepository.inmemory[F]
  pr <- ProductRepository.inmemory[F]
  plr <- ParameterListRepository.inmemory[F]
} yield Registry(cr, ir, ilr, mr, pr, plr)
