package com.freepper.catalog.app

import cats.{ApplicativeError, Monad, MonadError, MonadThrow, Traverse}
import cats.implicits.{catsSyntaxApplicativeError, toShow}
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import io.circe.{Decoder, Encoder, parser}
import scalapb.{FieldMaskUtil, GeneratedMessage, GeneratedMessageCompanion}
import squants.market.Money
import com.freepper.catalog.api
import com.freepper.catalog.app.ApiName.*
import com.freepper.catalog.domain.category.*
import com.freepper.catalog.domain.image.*
import com.freepper.catalog.domain.imageList.*
import com.freepper.catalog.domain.model.*
import com.freepper.catalog.domain.parameter.*
import com.freepper.catalog.domain.product.*
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import com.freepper.common.domain.crud.Query
import com.freepper.common.domain.crud.PageToken
import com.freepper.common.domain.error.NotFound

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}

trait ReadableIdsResolver[F[_]] {
  def resolveCategoryId(c: CategoryRefId): F[CategoryId]
  def resolveModelId(c: ModelRefId): F[ModelId]
}

object ReadableIdsResolver {
  case class RepoBasedResolver[F[_]: MonadThrow](categoryRepo: CategoryRepository[F], modelRepo: ModelRepository[F])
      extends ReadableIdsResolver[F] {
    override def resolveCategoryId(c: CategoryRefId): F[CategoryId] = c match {
      case Left(value) => MonadThrow[F].pure(value)
      case Right(value) =>
        categoryRepo.findAll(Query(CategorySelector.RidIs(value), PageToken.One)).flatMap {
          case Nil         => MonadThrow[F].raiseError(NotFound("category", value))
          case head :: Nil => Monad[F].pure(head.id)
          case _           => MonadThrow[F].raiseError(new IllegalStateException("got more than one record"))
        }
    }

    override def resolveModelId(c: ModelRefId): F[ModelId] = c match {
      case Left(value) => MonadThrow[F].pure(value)
      case Right(value) =>
        modelRepo.findAll(Query(ModelSelector.RidIs(value), PageToken.One)).flatMap {
          case Nil         => MonadThrow[F].raiseError(NotFound("model", value))
          case head :: Nil => Monad[F].pure(head.id)
          case _           => MonadThrow[F].raiseError(new IllegalStateException("got more than one record"))
        }
    }
  }
}
