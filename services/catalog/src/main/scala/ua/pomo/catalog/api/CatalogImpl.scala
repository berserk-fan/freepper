package ua.pomo.catalog.api

import io.grpc.Metadata

class CatalogImpl[F[_]] extends CatalogFs2Grpc[F, Metadata] {
  override def getCategory(request: GetCategoryRequest, ctx: Metadata): F[Category] = ???

  override def getProduct(request: GetProductRequest, ctx: Metadata): F[Product] = ???

  override def getModel(request: GetModelRequest, ctx: Metadata): F[Model] = ???

  override def listModels(request: ListModelsRequest, ctx: Metadata): F[ListModelsResponse] = ???

  override def listProducts(request: ListProductsRequest, ctx: Metadata): F[ListProductsResponse] = ???
}
