package ua.pomo.catalog.api

class CatalogImpl[F[_]] extends CatalogFs2Grpc[F, Unit] {
  override def getCategory(request: GetCategoryRequest, ctx: Unit): F[Category] = ???

  override def getProduct(request: GetProductRequest, ctx: Unit): F[Product] = ???

  override def getModel(request: GetModelRequest, ctx: Unit): F[Model] = ???

  override def listModels(request: ListModelsRequest, ctx: Unit): F[ListModelsResponse] = ???

  override def listProducts(request: ListProductsRequest, ctx: Unit): F[ListProductsResponse] = ???
}
