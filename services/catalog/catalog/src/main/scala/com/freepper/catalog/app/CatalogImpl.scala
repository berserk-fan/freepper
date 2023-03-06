package com.freepper.catalog.app

import cats.effect.kernel.Async
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, Status}
import org.typelevel.log4cats.LoggerFactory
import scalapb.validate.{Failure, Success, Validator}
import com.freepper.catalog.api._
import com.freepper.catalog.app.programs.ServiceMonad
import com.freepper.catalog.app.programs.modifiers.MessageModifier
import com.freepper.common.domain.registry.Registry
import com.freepper.common.domain.crud.{Crud, Service}
import com.freepper.common.domain.error._
import com.freepper.catalog.domain.RegistryHelper.implicits._
import com.freepper.common.domain.auth.CallContext
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

import scala.util.Try

case class CatalogImpl[F[_]: Async: LoggerFactory] private (
    services: Registry[Lambda[`T <: Crud` => Service[ServiceMonad[F, *], T]]],
    modifications: MessageModifier[F],
    converters: Converters[F]
) extends CatalogFs2Grpc[F, CallContext] {
  import com.freepper.common.app.CommonServiceMethods._
  // categories

  override def getCategory(request: GetCategoryRequest, ctx: CallContext): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.get(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def createCategory(request: CreateCategoryRequest, ctx: CallContext): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.create(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def deleteCategory(request: DeleteCategoryRequest, ctx: CallContext): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.delete(_).apply(ctx))
      .as(Empty())
  }

  override def updateCategory(request: UpdateCategoryRequest, ctx: CallContext): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.update(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  def listCategories(request: ListCategoriesRequest, ctx: CallContext): F[ListCategoriesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.findAll(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  // models

  override def getModel(request: GetModelRequest, ctx: CallContext): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.get(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def createModel(request: CreateModelRequest, ctx: CallContext): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.create(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def listModels(request: ListModelsRequest, ctx: CallContext): F[ListModelsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.findAll(_).apply(ctx))
      .flatMap(converters.toApiListModels)
  }

  override def deleteModel(request: DeleteModelRequest, ctx: CallContext): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.delete(_).apply(ctx))
      .as(Empty())
  }

  override def updateModel(request: UpdateModelRequest, ctx: CallContext): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.update(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  // products

  override def getProduct(request: GetProductRequest, ctx: CallContext): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.get(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def createProduct(request: CreateProductRequest, ctx: CallContext): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.create(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def listProducts(request: ListProductsRequest, ctx: CallContext): F[ListProductsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.findAll(_).apply(ctx))
      .flatMap(converters.toApiListProducts)
  }

  override def deleteProduct(request: DeleteProductRequest, ctx: CallContext): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.delete(_).apply(ctx))
      .as(Empty())
  }

  // imagelists

  override def createImageList(request: CreateImageListRequest, ctx: CallContext): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.create(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def getImageList(request: GetImageListRequest, ctx: CallContext): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.get(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def updateImageList(request: UpdateImageListRequest, ctx: CallContext): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.update(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  override def deleteImageList(request: DeleteImageListRequest, ctx: CallContext): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.delete(_).apply(ctx))
      .as(Empty())
  }

  override def listImageLists(request: ListImageListsRequest, ctx: CallContext): F[ListImageListsResponse] =
    adaptError {
      validate(request)
        .flatMap(_ => modifications.modify(request))
        .flatMap(converters.toDomain)
        .flatMap(services.imageList.findAll(_).apply(ctx))
        .flatMap(converters.toApiListImageLists)
    }

  def createImage(request: CreateImageRequest, ctx: CallContext): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.create(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  def deleteImage(request: DeleteImageRequest, ctx: CallContext): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.delete(_).apply(ctx))
      .as(Empty())
  }

  def getImage(request: GetImageRequest, ctx: CallContext): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.get(_).apply(ctx))
      .flatMap(converters.toApi)
  }

  def listImages(request: ListImagesRequest, ctx: CallContext): F[ListImagesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.findAll(_).apply(ctx))
      .flatMap(converters.toApiListImages)
  }

}
