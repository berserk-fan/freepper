package com.freepper.catalog.app

import cats.{ApplicativeError, ApplicativeThrow, Monad, MonadError, MonadThrow, Traverse}
import cats.implicits.{catsSyntaxApplicativeError, toShow}
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import io.circe.{Decoder, Encoder, parser}
import scalapb.{FieldMaskUtil, GeneratedMessage, GeneratedMessageCompanion}
import squants.market.Money
import com.freepper.catalog.api
import com.freepper.catalog.api.{
  CreateCategoryRequest,
  CreateImageListRequest,
  CreateImageRequest,
  CreateModelRequest,
  CreateProductRequest,
  DeleteCategoryRequest,
  DeleteImageListRequest,
  DeleteImageRequest,
  DeleteModelRequest,
  DeleteProductRequest,
  GetCategoryRequest,
  GetImageListRequest,
  GetImageRequest,
  GetModelRequest,
  GetProductRequest,
  ListCategoriesRequest,
  ListCategoriesResponse,
  ListImageListsRequest,
  ListImageListsResponse,
  ListImagesRequest,
  ListImagesResponse,
  ListModelsRequest,
  ListModelsResponse,
  ListProductsRequest,
  ListProductsResponse,
  UpdateCategoryRequest,
  UpdateImageListRequest,
  UpdateModelRequest
}
import com.freepper.catalog.app.ApiName.*
import com.freepper.catalog.domain.category.*
import com.freepper.catalog.domain.image.*
import com.freepper.catalog.domain.imageList.*
import com.freepper.catalog.domain.model.*
import com.freepper.catalog.domain.parameter.*
import com.freepper.catalog.domain.product.*
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}

trait UUIDGenerator[F[_]] {
  def gen: F[UUID]
  def fromString(s: String): F[UUID]
}

object UUIDGenerator {
  def fromApplicativeError[F[_]: ApplicativeThrow]: UUIDGenerator[F] = new UUIDGenerator[F] {
    override def gen: F[UUID] = ApplicativeThrow[F].catchNonFatal(UUID.randomUUID())

    override def fromString(s: String): F[UUID] = ApplicativeThrow[F].catchNonFatal(UUID.fromString(s))
  }
}
