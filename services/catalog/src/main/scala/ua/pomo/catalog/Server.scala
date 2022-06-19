package ua.pomo.catalog

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import cats.kernel.Monoid
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
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

object Server extends IOApp.Simple {
  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory[IO]

  def of(
      config: AppConfig,
      imageDataRepositoryLifted: IO[ImageDataRepository[IO]]
  ): IO[Resource[IO, io.grpc.Server]] = {
    for {
      logger <- LoggerFactory[IO].create
      _ <- logger.info("Starting up server...")
      transactor = TransactorHelpers.fromConfig[IO](config.jdbc)
      categoryRepo = CategoryRepositoryImpl()
      categoryService = CategoryServiceImpl(transactor, categoryRepo)
      imageListService = ImageListServiceImpl(transactor, ImageListRepositoryImpl())
      modelService = ModelServiceImpl(transactor, ModelRepositoryImpl())
      productService = ProductServiceImpl(transactor, ProductRepositoryImpl())
      imageDataRepository <- imageDataRepositoryLifted
      imageService = ImageServiceImpl(ImageRepositoryImpl, imageDataRepository, transactor.trans)
      resolver = ReadableIdInNamesResolver[IO](CategoryRepositoryImpl.withEffect[IO](transactor.trans))
      pageDefaultsApplier = PageDefaultsApplier[IO](config.catalog.defaultPageSize)
      modifier = Monoid[MessageModifier[IO]].combineAll(List(resolver, pageDefaultsApplier))
      catalogService = CatalogFs2Grpc.bindServiceResource[IO](
        CatalogImpl(productService, categoryService, modelService, imageListService, imageService, modifier)
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

  def prodServer: IO[Resource[IO, io.grpc.Server]] = {
    for {
      config <- AppConfig.loadDefault[IO]
      server <- of(config, S3ImageDataRepository[IO](config.aws))
    } yield server
  }

  override def run: IO[Nothing] = {
    prodServer.flatMap(_.useForever)
  }
}
