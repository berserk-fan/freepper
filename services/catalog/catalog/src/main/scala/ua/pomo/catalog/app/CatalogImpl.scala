package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, Status}
import org.typelevel.log4cats.LoggerFactory
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.api._
import ua.pomo.catalog.app.programs.ServiceMonad
import ua.pomo.catalog.app.programs.modifiers.MessageModifier
import ua.pomo.common.domain.registry.Registry
import ua.pomo.common.domain.crud.{Crud, Service}
import ua.pomo.common.domain.error._
import ua.pomo.catalog.domain.RegistryHelper.implicits._
import ua.pomo.common.domain.auth.CallContext

import scala.util.Try

case class CatalogImpl[F[_]: Async: LoggerFactory] private (
    services: Registry[Lambda[`T <: Crud` => Service[ServiceMonad[F, *], T]]],
    modifications: MessageModifier[F],
    converters: Converters[F]
) extends CatalogFs2Grpc[F, CallContext] {

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

  override def listImageLists(request: ListImageListsRequest, ctx: CallContext): F[ListImageListsResponse] = adaptError {
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
