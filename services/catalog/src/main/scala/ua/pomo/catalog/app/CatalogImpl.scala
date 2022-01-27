package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.effect.Resource
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.{Metadata, ServerServiceDefinition, Status}
import scalapb.validate.{Failure, Success, Validator}
import ua.pomo.catalog.CatalogApiConfig
import ua.pomo.catalog.domain.{PageToken, category, model}
import ua.pomo.catalog.api._
import ua.pomo.catalog.domain.error._
import ua.pomo.catalog.domain.model.FindModel

object CatalogImpl {
  def apply[F[_]: Async](
      categoryService: category.CategoryService[F],
      modelService: model.ModelService[F],
      config: CatalogApiConfig
  ): CatalogFs2Grpc[F, Metadata] = {
    new CatalogImpl[F](categoryService, modelService, config)
  }

  def makeGrpc[F[_]: Async](
      categoryService: category.CategoryService[F],
      modelService: model.ModelService[F],
      config: CatalogApiConfig
  ): Resource[F, ServerServiceDefinition] = {
    CatalogFs2Grpc.bindServiceResource[F](CatalogImpl[F](categoryService, modelService, config))
  }

  private class CatalogImpl[F[_]: Async](categoryService: category.CategoryService[F],
                                         modelService: model.ModelService[F],
                                         config: CatalogApiConfig)
      extends CatalogFs2Grpc[F, Metadata] {
    override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = {
      Async[F]
        .fromEither(ApiName.category(request.name))
        .flatMap(name => categoryService.get(name.categoryId))
        .map(Converters.toApi)
    }

    override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = adaptError {
      validate(request) >>
        Async[F]
          .fromEither(ApiName.model(request.name))
          .flatMap(name => modelService.get(name.modelId))
          .map(Converters.toApi)
    }

    override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = ???

    override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = ???

    override def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = {
      modelService.create(Converters.toDomain(request)).map(Converters.toApi)
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

    override def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = ???
    override def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = ???

    override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = ???

    override def createCategory(request: CreateCategoryRequest, ctx: Metadata): F[Category] = validate(request) >> {
      categoryService.createCategory(Converters.toDomain(request)).map(Converters.toApi)
    }

    private def validate[T: Validator, U](t: T): F[Unit] = Validator[T].validate(t) match {
      case Success => ().pure[F]
      case Failure(violations) =>
        ValidationErr(violations.mkString(",")).raiseError[F, Unit]
    }

    private def applyDefaults(request: FindModel): FindModel = request.copy(page = applyDefaults(request.page))
    private def applyDefaults(pageToken: PageToken.NotEmpty): PageToken.NotEmpty = {
      val newSize = if (pageToken.size == 0) config.defaultPageSize.toLong else pageToken.size
      pageToken.copy(size = newSize)
    }

    private def adaptError[T](f: F[T]): F[T] = f.adaptError {
      case e: Err =>
        val status = e match {
          case ValidationErr(_, _) => Status.INVALID_ARGUMENT
          case DbErr(_, _)         => Status.INTERNAL
          case NotFound(_, _)      => Status.NOT_FOUND
        }
        status.withDescription(e.getMessage).withCause(e.getCause).asException()
    }
  }
}
