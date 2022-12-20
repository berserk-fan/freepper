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
import ua.pomo.catalog.domain.Registry
import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.ImageCrud
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.domain.repository.{Crud, PageToken, Repository}

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
    new Registry[Lambda[`T <: Crud` => Repository[ConnectionIO, T]]] {
      override def category: Repository[ConnectionIO, CategoryCrud] = CategoryRepository.postgres

      override def image: Repository[ConnectionIO, ImageCrud] = ImageRepository.postgres

      override def imageList: Repository[ConnectionIO, ImageListCrud] = ImageListRepository.postgres

      override def model: Repository[ConnectionIO, ModelCrud] = ModelRepository.postgres

      override def product: Repository[ConnectionIO, ProductCrud] = ProductRepository.postgres

      override def parameterList: Repository[ConnectionIO, ParameterListCrud] = ParameterListRepository.postgres
    }

  def inMemoryRepoRegistry[F[_]: Sync]: F[Registry[Lambda[`T <: Crud` => Repository[F, T]]]] = for {
    cr <- CategoryRepository.inmemory[F]
    ilr <- ImageListRepository.inmemory[F]
    mr <- ModelRepository.inmemory[F]
    pr <- ProductRepository.inmemory[F]
  } yield new Registry[Lambda[`T <: Crud` => Repository[F, T]]] {
    override def category: Repository[F, CategoryCrud] = cr

    override def image: Repository[F, ImageCrud] = ???

    override def imageList: Repository[F, ImageListCrud] = ilr

    override def model: Repository[F, ModelCrud] = mr

    override def product: Repository[F, ProductCrud] = pr

    override def parameterList: Repository[F, ParameterListCrud] = ???
  }

}
