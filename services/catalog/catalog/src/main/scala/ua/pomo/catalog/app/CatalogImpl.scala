package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, Status}
import org.typelevel.log4cats.LoggerFactory
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.api._
import ua.pomo.catalog.app.programs.modifiers.MessageModifier
import ua.pomo.catalog.domain.Registry
import ua.pomo.common.domain.crud.{Crud, Service}
import ua.pomo.common.domain.error._

import scala.util.Try

case class CatalogImpl[F[_]: Async: LoggerFactory] private (
    services: Registry[Lambda[`T <: Crud` => Service[F, T]]],
    modifications: MessageModifier[F],
    converters: Converters[F]
) extends CatalogFs2Grpc[F, Metadata] {

  // categories

  override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.get)
      .flatMap(converters.toApi)
  }

  override def createCategory(request: CreateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.create)
      .flatMap(converters.toApi)
  }

  override def deleteCategory(request: DeleteCategoryRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.delete)
      .as(Empty())
  }

  override def updateCategory(request: UpdateCategoryRequest, ctx: Metadata): F[Category] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.update)
      .flatMap(converters.toApi)
  }

  def listCategories(request: ListCategoriesRequest, ctx: Metadata): F[ListCategoriesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.category.findAll)
      .flatMap(converters.toApi)
  }

  // models

  override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.get)
      .flatMap(converters.toApi)
  }

  override def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.create)
      .flatMap(converters.toApi)
  }

  override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.findAll)
      .flatMap(converters.toApiListModels)
  }

  override def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.delete)
      .as(Empty())
  }

  override def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.model.update)
      .flatMap(converters.toApi)
  }

  // products

  override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.get)
      .flatMap(converters.toApi)
  }

  override def createProduct(request: CreateProductRequest, ctx: Metadata): F[Product] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.create)
      .flatMap(converters.toApi)
  }

  override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.findAll)
      .flatMap(converters.toApiListProducts)
  }

  override def deleteProduct(request: DeleteProductRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.product.delete)
      .as(Empty())
  }

  // imagelists

  override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.create)
      .flatMap(converters.toApi)
  }

  override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.get)
      .flatMap(converters.toApi)
  }

  override def updateImageList(request: UpdateImageListRequest, ctx: Metadata): F[ImageList] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.update)
      .flatMap(converters.toApi)
  }

  override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.delete)
      .as(Empty())
  }

  override def listImageLists(request: ListImageListsRequest, ctx: Metadata): F[ListImageListsResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.imageList.findAll)
      .flatMap(converters.toApiListImageLists)
  }

  def createImage(request: CreateImageRequest, ctx: Metadata): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.create)
      .flatMap(converters.toApi)
  }

  def deleteImage(request: DeleteImageRequest, ctx: Metadata): F[Empty] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.delete)
      .as(Empty())
  }

  def getImage(request: GetImageRequest, ctx: Metadata): F[Image] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.get)
      .flatMap(converters.toApi)
  }

  def listImages(request: ListImagesRequest, ctx: Metadata): F[ListImagesResponse] = adaptError {
    validate(request)
      .flatMap(_ => modifications.modify(request))
      .flatMap(converters.toDomain)
      .flatMap(services.image.findAll)
      .flatMap(converters.toApiListImages)
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
