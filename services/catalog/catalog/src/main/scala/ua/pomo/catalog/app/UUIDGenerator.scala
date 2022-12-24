package ua.pomo.catalog.app

import cats.{ApplicativeError, ApplicativeThrow, Monad, MonadError, MonadThrow, Traverse}
import cats.implicits.{catsSyntaxApplicativeError, toShow}
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import io.circe.{Decoder, Encoder, parser}
import scalapb.{FieldMaskUtil, GeneratedMessage, GeneratedMessageCompanion}
import squants.market.Money
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{
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
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.product._
import ua.pomo.common.domain.crud.{ListResponse, PageToken, Query}
import ua.pomo.common.domain.error.{NotFound, ValidationErr}
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
