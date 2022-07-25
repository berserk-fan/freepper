package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, Status}
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.domain.{category, model, product}
import ua.pomo.catalog.api._
import ua.pomo.common.domain.error._
import ua.pomo.catalog.domain.imageList.ImageListService

import scala.util.Try
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.app.programs.modifiers.MessageModifier
import ua.pomo.catalog.domain.image.ImageService

case class CatalogImpl[F[_]: Async: LoggerFactory] private (
    productService: product.ProductService[F],
    categoryService: category.CategoryService[F],
    modelService: model.ModelService[F],
    imageListService: ImageListService[F],
    imageService: ImageService[F],
    modifications: MessageModifier[F]
) extends CatalogFs2Grpc[F, Metadata] {

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

  override def listImageLists(request: ListImageListsRequest, ctx: Metadata): F[ListImageListsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageListService.find)
      .map(Converters.toApi)
  }

  def createImage(request: CreateImageRequest, ctx: Metadata): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageService.create)
      .map(Converters.toApi)
  }

  def deleteImage(request: DeleteImageRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageService.delete)
      .as(Empty())
  }

  def getImage(request: GetImageRequest, ctx: Metadata): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageService.get)
      .map(Converters.toApi)
  }

  def listImages(request: ListImagesRequest, ctx: Metadata): F[ListImagesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .map(Converters.toDomain)
      .flatMap(imageService.query)
      .map(Converters.toApi)
  }

  private def adaptError[T](f: => F[T]): F[T] = for {
    logger <- LoggerFactory[F].create
    res <- Async[F]
      .fromEither(Try(f).toEither.leftMap(err => ImpureError("Api method has thrown an error: impure", Some(err))))
      .flatten
      .onError {
        case e: ValidationErr => logger.info(e)("Api method: Validation request error")
        case e: NotFound      => logger.info(e)("Api method: Not found error")
        case e                => logger.error(e)("Api method: Severe error")
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
  } yield res

  private def validate[T: Validator](t: T): F[Unit] = Validator[T].validate(t) match {
    case Success => ().pure[F]
    case Failure(violations) =>
      ValidationErr(violations.mkString(",")).raiseError[F, Unit]
  }
}
