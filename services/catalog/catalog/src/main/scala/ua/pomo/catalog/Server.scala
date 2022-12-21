package ua.pomo.catalog

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.kernel.Monoid
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.api.CatalogFs2Grpc
import ua.pomo.catalog.app.CatalogImpl
import ua.pomo.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier, ReadableIdInNamesResolver}
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
import ua.pomo.common.infrastracture.persistance.RepositoryK

abstract class Server {

  def imageDataRepositoryResource(config: AppConfig)(implicit
      lf: LoggerFactory[IO]
  ): Resource[IO, ImageDataRepository[IO]]

  def serviceResource(
      config: AppConfig
  )(implicit e: LoggerFactory[IO]): Resource[IO, ServerServiceDefinition] = {
    for {
      logger <- Resource.eval(LoggerFactory[IO].create)
      _ <- Resource.eval(logger.info("Creating catalog service..."))
      transactor = TransactorHelpers.fromConfig[IO](config.jdbc)
      categoryRepo = CategoryRepository.postgres
      categoryService = CategoryServiceImpl(transactor, categoryRepo)
      imageListService = ImageListServiceImpl(transactor, ImageListRepository.postgres)
      modelService = ModelServiceImpl(transactor, ModelRepository.postgres)
      productService = ProductServiceImpl(transactor, ProductRepository.postgres)
      imageDataRepository <- imageDataRepositoryResource(config)
      imageService = ImageServiceImpl(ImageRepository.postgres, imageDataRepository, transactor.trans)
      resolver = ReadableIdInNamesResolver[IO](RepositoryK(CategoryRepository.postgres, transactor.trans))
      pageDefaultsApplier = PageDefaultsApplier[IO](config.api.defaultPageSize)
      modifier = Monoid[MessageModifier[IO]].combineAll(List(resolver, pageDefaultsApplier))
      catalogService <- CatalogFs2Grpc.bindServiceResource[IO](
        CatalogImpl(productService, categoryService, modelService, imageListService, imageService, modifier)
      )
    } yield catalogService
  }

  def serverResource(config: AppConfig)(implicit e: LoggerFactory[IO]): Resource[IO, io.grpc.Server] = {
    for {
      service <- serviceResource(config)
      res <- NettyServerBuilder
        .forPort(config.server.serverPort)
        .addService(service)
        .resource[IO]
        .evalMap(server => IO(server.start()))
    } yield res
  }
}

object Server {
  def production: Server = new Server {
    override def imageDataRepositoryResource(
        config: AppConfig
    )(implicit lf: LoggerFactory[IO]): Resource[IO, ImageDataRepository[IO]] = {
      Resource.eval { S3ImageDataRepository[IO](config.aws) }
    }
  }
}
