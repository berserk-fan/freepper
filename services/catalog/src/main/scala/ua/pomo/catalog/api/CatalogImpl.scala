package ua.pomo.catalog.api

import cats.effect.kernel.Async
import cats.implicits._
import com.google.protobuf.empty.Empty
import io.grpc.Metadata
import ua.pomo.catalog.domain.category

object CatalogImpl {
  def apply[F[_]: Async](categoryService: category.CategoryService[F]): CatalogFs2Grpc[F, Metadata] = {
    new CatalogImpl[F](categoryService)
  }

  private class CatalogImpl[F[_]: Async](categoryService: category.CategoryService[F]) extends CatalogFs2Grpc[F, Metadata] {
    override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = {
      categoryService
        .getCategory(NameParser.parseCategoryName(request.name))
        .map(Converters.fromDomain(request.name, _))
    }

    override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = ???

    override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = ???

    override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = ???

    override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = ???

    override def createImageList(request: CreateImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def getImageList(request: GetImageListRequest, ctx: Metadata): F[ImageList] = ???

    override def deleteImageList(request: DeleteImageListRequest, ctx: Metadata): F[Empty] = ???
  }

}