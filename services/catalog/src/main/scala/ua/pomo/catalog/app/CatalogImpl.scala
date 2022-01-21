package ua.pomo.catalog.app

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.Metadata
import ua.pomo.catalog.api.{CatalogFs2Grpc, Category, CreateImageListRequest, DeleteImageListRequest, GetCategoryRequest, GetImageListRequest, GetModelRequest, GetProductRequest, ImageList, ListModelsRequest, ListModelsResponse, ListProductsRequest, ListProductsResponse, Model, Product}
import ua.pomo.catalog.domain.{category, model}
import ua.pomo.catalog.api._

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
        .fromEither(CategoryNameModule.of(request.name).leftMap(new Exception(_)))
        .flatMap(name => categoryService.getCategory(name.categoryId))
        .map(Converters.fromDomain(request.name, _))
    }

    override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = ???

    override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = ???

    override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = ???

    override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = ???

    override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = ???

    def createModel(request: CreateModelRequest, ctx: Metadata): F[Model] = ???
    def deleteModel(request: DeleteModelRequest, ctx: Metadata): F[Empty] = ???
    def updateModel(request: UpdateModelRequest, ctx: Metadata): F[Model] = ???
  }
}
