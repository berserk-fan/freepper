package ua.pomo.catalog

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import ua.pomo.catalog.api.CatalogFs2Grpc
import ua.pomo.catalog.app.programs.modifiers.{PageDefaultsApplier, ReadableIdInNamesResolver}
import ua.pomo.catalog.app.CatalogImpl
import ua.pomo.catalog.app.programs.{CategoryServiceImpl, ImageListServiceImpl, ModelServiceImpl, ProductServiceImpl}
import ua.pomo.catalog.infrastructure.persistance.{
  CategoryRepositoryImpl,
  ImageListRepositoryImpl,
  ModelRepositoryImpl,
  ProductRepositoryImpl
}

object Server extends IOApp.Simple {

  def resource(config: AppConfig): IO[Resource[IO, io.grpc.Server]] = {
    for {
      _ <- IO.unit
      transactor = TransactorHelpers.fromConfig[IO](config.jdbc)
      categoryRepo = CategoryRepositoryImpl()
      categoryService = CategoryServiceImpl(transactor, categoryRepo)
      imageListService = ImageListServiceImpl(transactor, ImageListRepositoryImpl())
      modelService = ModelServiceImpl(transactor, ModelRepositoryImpl())
      productService = ProductServiceImpl(transactor, ProductRepositoryImpl())
      resolver = ReadableIdInNamesResolver[IO](CategoryRepositoryImpl.withEffect[IO](transactor.trans))
      pageDefaultsApplier = PageDefaultsApplier[IO](config.catalog.defaultPageSize)
      catalogService = CatalogFs2Grpc.bindServiceResource[IO](
        CatalogImpl(productService, categoryService, modelService, imageListService, resolver, pageDefaultsApplier)
      )
    } yield catalogService.flatMap { service =>
      NettyServerBuilder
        .forPort(config.server.serverPort)
        .addService(service)
        .addService(ProtoReflectionService.newInstance())
        .resource[IO]
        .evalMap(server => IO(server.start()))
    }
  }

  override def run: IO[Nothing] = AppConfig.loadDefault[IO].flatMap(resource).flatMap(_.useForever)
}
