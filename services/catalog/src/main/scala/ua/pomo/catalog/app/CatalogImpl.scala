package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.effect.Resource
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, ServerServiceDefinition, Status}
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.CatalogApiConfig
import ua.pomo.catalog.domain.{PageToken, category, model, product}
import ua.pomo.catalog.api._
import ua.pomo.catalog.domain.error._
import ua.pomo.catalog.domain.image.ImageListService
import ua.pomo.catalog.domain.model.ModelQuery
import ua.pomo.catalog.domain.product.ProductService

import scala.util.Try

class CatalogImpl[F[_]: Async] private (productService: product.ProductService[F],
                                        categoryService: category.CategoryService[F],
                                        modelService: model.ModelService[F],
                                        imageListService: ImageListService[F],
                                        config: CatalogApiConfig)
    extends CatalogFs2Grpc[F, Metadata] {

  //categories

  override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request) >> {
      Async[F]
        .fromEither(ApiName.category(request.name))
        .flatMap(name => categoryService.get(name.categoryId))
        .map(Converters.toApi)
    }
  }

  override def createCategory(request: CreateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request) >>
      categoryService.create(Converters.toDomain(request)).map(Converters.toApi)
  }

  override def deleteCategory(request: DeleteCategoryRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request) >>
      Async[F]
        .fromEither(ApiName.category(request.name))
        .flatMap(name => categoryService.delete(name.categoryId))
        .as(Empty())
  }

  override def updateCategory(request: UpdateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request) >>
      categoryService
        .update(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  //models

  override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request) >>
      Async[F]
        .fromEither(ApiName.model(request.name))
        .flatMap(name => modelService.get(name.modelId))
        .map(Converters.toApi)
  }

  override def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request) >>
      modelService
        .create(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = adaptError {
    validate(request) >> {
      Async[F]
        .fromTry(Converters.toDomain(request))
        .map(applyDefaults)
        .flatMap(modelService.findAll)
        .map(Converters.toApi)
    }
  }

  override def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request) >>
      modelService
        .delete(Converters.toDomain(request))
        .as(Empty())
  }

  override def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request) >>
      modelService
        .update(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  //products

  override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request) >>
      productService
        .get(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def createProduct(request: CreateProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request) >>
      productService
        .create(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = adaptError {
    validate(request) >>
      Async[F]
        .fromTry(Converters.toDomain(request))
        .flatMap(productService.query)
        .map(Converters.toApi)
  }

  override def deleteProduct(request: DeleteProductRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request) >>
      productService
        .delete(Converters.toDomain(request))
        .as(Empty())
  }

  //imagelists

  override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request) >>
      imageListService
        .create(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request) >>
      imageListService
        .get(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def updateImageList(request: UpdateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request) >>
      imageListService
        .update(Converters.toDomain(request))
        .map(Converters.toApi)
  }

  override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request) >>
      imageListService
        .delete(Converters.toDomain(request))
        .as(Empty())
  }

  override def listImageLists(request: ListImageListsRequest, ctx: Metadata): F[ListImageListsResponse] = ???

  private def applyDefaults(request: ModelQuery): ModelQuery = request.copy(page = applyDefaults(request.page))
  private def applyDefaults(pageToken: PageToken.NonEmpty): PageToken.NonEmpty = {
    val newSize = if (pageToken.size == 0) config.defaultPageSize.toLong else pageToken.size
    pageToken.copy(size = newSize)
  }

  private def adaptError[T](f: => F[T]): F[T] = {
    Async[F]
      .fromTry(Try(f))
      .flatten
      .adaptError { e =>
        val status = e match {
          case ValidationErr(_, _) => Status.INVALID_ARGUMENT
          case DbErr(_, _)         => Status.INTERNAL
          case NotFound(_, _, _)   => Status.NOT_FOUND
          case _                   => Status.INTERNAL
        }
        status.withDescription(s"${e.getClass.getSimpleName} ${e.getMessage}").withCause(e).asException()
      }
  }

  private def validate[T: Validator, U](t: T): F[Unit] = Validator[T].validate(t) match {
    case Success => ().pure[F]
    case Failure(violations) =>
      ValidationErr(violations.mkString(",")).raiseError[F, Unit]
  }
}

object CatalogImpl {
  def apply[F[_]: Async](
      productService: ProductService[F],
      categoryService: category.CategoryService[F],
      modelService: model.ModelService[F],
      imageListService: ImageListService[F],
      config: CatalogApiConfig
  ): CatalogFs2Grpc[F, Metadata] = {
    new CatalogImpl[F](productService, categoryService, modelService, imageListService, config)
  }

  def makeGrpc[F[_]: Async](
      productService: ProductService[F],
      categoryService: category.CategoryService[F],
      modelService: model.ModelService[F],
      imageListService: ImageListService[F],
      config: CatalogApiConfig
  ): Resource[F, ServerServiceDefinition] = {
    CatalogFs2Grpc.bindServiceResource[F](
      CatalogImpl[F](productService, categoryService, modelService, imageListService, config))
  }
}
