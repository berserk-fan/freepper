package ua.pomo.catalog.app

import cats.{ApplicativeError, Monad, MonadError, MonadThrow, Traverse}
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
import ua.pomo.common.domain.error.{ValidationErr, NotFound}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

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
