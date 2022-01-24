package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.Metadata
import ua.pomo.catalog.api.{CatalogFs2Grpc, Category, CreateImageListRequest, DeleteImageListRequest, GetCategoryRequest, GetImageListRequest, GetModelRequest, GetProductRequest, ImageList, ListModelsRequest, ListModelsResponse, ListProductsRequest, ListProductsResponse, Model, Product}
import ua.pomo.catalog.domain.{category, model}
import ua.pomo.catalog.api._
import ua.pomo.catalog.domain.category.CategoryId
import ua.pomo.catalog.domain.model.FindModel

object CatalogImpl {
  def apply[F[_]: Async](categoryService: category.CategoryService[F],
                         modelService: model.ModelService[F]): CatalogFs2Grpc[F, Metadata] = {
    new CatalogImpl[F](categoryService, modelService)
  }

  private class CatalogImpl[F[_]: Async](categoryService: category.CategoryService[F],
                                         modelService: model.ModelService[F])
      extends CatalogFs2Grpc[F, Metadata] {
    override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = {
      Async[F]
        .fromEither(ApiName.category(request.name))
        .flatMap(name => categoryService.get(name.categoryId))
        .map(Converters.toApi)
    }

    override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = {
      Async[F]
        .fromEither(ApiName.model(request.name))
        .flatMap(name => modelService.get(name.modelId))
        .map(Converters.toApi)
    }

    override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = ???

    override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = ???

    private def checkIsNotWildcardAndGetCategoryId(name: ModelsName): F[CategoryId] = {
      name.categoryId.fold(new Exception("wildCard get models not supported").raiseError[F, CategoryId])(_.pure[F])
    }

    override def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = {
      Async[F]
        .fromEither(ApiName.models(request.parent))
        .flatMap(checkIsNotWildcardAndGetCategoryId)
        .flatMap(categoryService.get)
        .map(_.id)
        .map(categoryUUID => Converters.toDomain(request, categoryUUID))
        .flatMap(modelService.create)
        .map(Converters.toApi)
    }

    override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] =
      Async[F]
        .fromEither(ApiName.models(request.parent))
        .flatMap(checkIsNotWildcardAndGetCategoryId)
        .flatMap(id => categoryService.get(id))
        .map(_.id)
        .flatMap(id => modelService.findAll(FindModel(id, request.pageSize.toLong, 0)))
        .map(models => ListModelsResponse(models.map(Converters.toApi)))

    override def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = ???
    override def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = ???

    override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = ???

    override def createCategory(request: CreateCategoryRequest, ctx: Metadata): F[Category] = {
      categoryService.createCategory(Converters.toDomain(request)).map(Converters.toApi)
    }
  }
}
