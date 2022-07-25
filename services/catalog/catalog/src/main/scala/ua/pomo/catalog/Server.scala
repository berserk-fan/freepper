package ua.pomo.catalog

import cats.effect.kernel.Resource
import cats.effect.IO
import cats.kernel.Monoid
import io.grpc.ServerServiceDefinition
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.api.CatalogFs2Grpc
import ua.pomo.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier, ReadableIdInNamesResolver}
import ua.pomo.catalog.app.CatalogImpl
import ua.pomo.catalog.app.programs.{
  CategoryServiceImpl,
  ImageListServiceImpl,
  ImageServiceImpl,
  ModelServiceImpl,
  ProductServiceImpl
}
import ua.pomo.catalog.domain.image.ImageDataRepository
import ua.pomo.catalog.infrastructure.persistance.postgres._
import ua.pomo.catalog.infrastructure.persistance.s3.S3ImageDataRepository
import ua.pomo.common.TransactorHelpers

object Server {
  private def serviceResource(
      config: AppConfig,
      imageDataRepositoryLifted: IO[ImageDataRepository[IO]]
  )(implicit e: LoggerFactory[IO]): IO[Resource[IO, ServerServiceDefinition]] = {
    for {
      logger <- LoggerFactory[IO].create
      _ <- logger.info("Creating catalog service...")
      transactor = TransactorHelpers.fromConfig[IO](config.jdbc)
      categoryRepo = CategoryRepositoryImpl()
      categoryService = CategoryServiceImpl(transactor, categoryRepo)
      imageListService = ImageListServiceImpl(transactor, ImageListRepositoryImpl())
      modelService = ModelServiceImpl(transactor, ModelRepositoryImpl())
      productService = ProductServiceImpl(transactor, ProductRepositoryImpl())
      imageDataRepository <- imageDataRepositoryLifted
      imageService = ImageServiceImpl(ImageRepositoryImpl, imageDataRepository, transactor.trans)
      resolver = ReadableIdInNamesResolver[IO](CategoryRepositoryImpl.withEffect[IO](transactor.trans))
      pageDefaultsApplier = PageDefaultsApplier[IO](config.api.defaultPageSize)
      modifier = Monoid[MessageModifier[IO]].combineAll(List(resolver, pageDefaultsApplier))
      catalogService = CatalogFs2Grpc.bindServiceResource[IO](
        CatalogImpl(productService, categoryService, modelService, imageListService, imageService, modifier)
      )
    } yield catalogService
  }

  def serverResource(
      config: AppConfig,
      imageDataRepositoryLifted: IO[ImageDataRepository[IO]]
  )(implicit e: LoggerFactory[IO]): IO[Resource[IO, io.grpc.Server]] = {
    for {
      service <- serviceResource(config, imageDataRepositoryLifted)
    } yield service.flatMap { s =>
      NettyServerBuilder
        .forPort(config.server.serverPort)
        .addService(s)
        .resource[IO]
        .evalMap(server => IO(server.start()))
    }
  }

  def prodService(
      config: AppConfig
  )(implicit e: LoggerFactory[IO]): IO[Resource[IO, io.grpc.ServerServiceDefinition]] = {
    for {
      service <- Server.serviceResource(config, S3ImageDataRepository[IO](config.aws))
    } yield service
  }
}
