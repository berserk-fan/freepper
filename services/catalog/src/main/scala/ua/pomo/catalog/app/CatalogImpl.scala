package ua.pomo.catalog.app

import cats.Monoid
import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, Status}
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.domain.{category, model, product}
import ua.pomo.catalog.api._
import ua.pomo.catalog.domain.error._
import ua.pomo.catalog.domain.image.ImageListService

import scala.util.Try
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ua.pomo.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier, ReadableIdInNamesResolver}

case class CatalogImpl[F[_]: Async] private (
    productService: product.ProductService[F],
    categoryService: category.CategoryService[F],
    modelService: model.ModelService[F],
    imageListService: ImageListService[F],
    readableIdResolver1: ReadableIdInNamesResolver[F],
    pageDefaultsApplier: PageDefaultsApplier[F]
) extends CatalogFs2Grpc[F, Metadata] {

  private val modifications = Monoid[MessageModifier[F]].combineAll(List(readableIdResolver1, pageDefaultsApplier))

  implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  // categories

  override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(categoryService.get)
      .map(Converters.toApi)
  }

  override def createCategory(request: CreateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(categoryService.create)
      .map(Converters.toApi)
  }

  override def deleteCategory(request: DeleteCategoryRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(categoryService.delete)
      .as(Empty())
  }

  override def updateCategory(request: UpdateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(categoryService.update)
      .map(Converters.toApi)
  }

  def listCategories(request: ListCategoriesRequest, ctx: Metadata): F[ListCategoriesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(categoryService.query)
      .map(Converters.toApi)
  }

  // models

  override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(modelService.get)
      .map(Converters.toApi)
  }

  override def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(modelService.create)
      .map(Converters.toApi)
  }

  override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(modelService.findAll)
      .map(Converters.toApi)
  }

  override def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(modelService.delete)
      .as(Empty())
  }

  override def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(modelService.update)
      .map(Converters.toApi)
  }

  // products

  override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(productService.get)
      .map(Converters.toApi)
  }

  override def createProduct(request: CreateProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(productService.create)
      .map(Converters.toApi)
  }

  override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(productService.query)
      .map(Converters.toApi)
  }

  override def deleteProduct(request: DeleteProductRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(productService.delete)
      .as(Empty())
  }

  // imagelists

  override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageListService.create)
      .map(Converters.toApi)
  }

  override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageListService.get)
      .map(Converters.toApi)
  }

  override def updateImageList(request: UpdateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageListService.update)
      .map(Converters.toApi)
  }

  override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageListService.delete)
      .as(Empty())
  }

  override def listImageLists(request: ListImageListsRequest, ctx: Metadata): F[ListImageListsResponse] = ???

  private def adaptError[T](f: => F[T]): F[T] = {
    Async[F]
      .fromEither(Try(f).toEither.leftMap(err => UnexpectedError("Api method has thrown an error: impure", Some(err))))
      .flatten
      .onError { e =>
        logger.error(e)("Api method resulted in an error")
      }
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