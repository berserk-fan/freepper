package ua.pomo.catalog

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.kernel.Monoid
import doobie.ConnectionIO
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.api.CatalogFs2Grpc
import ua.pomo.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier}
import ua.pomo.catalog.app.{CatalogImpl, Converters, ReadableIdsResolver, UUIDGenerator, programs}
import ua.pomo.catalog.domain.Registry
import ua.pomo.catalog.domain.image.ImageDataRepository
import ua.pomo.catalog.infrastructure.persistance.postgres._
import ua.pomo.catalog.infrastructure.persistance.s3.S3ImageDataRepository
import ua.pomo.common.TransactorHelpers
import ua.pomo.common.domain.crud.{Crud, Service}
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
      imageDataRepository <- imageDataRepositoryResource(config)
      repos = postgresRepoRegistry
      services: Registry[Lambda[`T <: Crud` => Service[IO, T]]] = programs.serviceRegistry[ConnectionIO, IO](
        repos,
        transactor.trans,
        imageDataRepository
      )
      pageDefaultsApplier = PageDefaultsApplier[IO](config.api.defaultPageSize)
      modifier = Monoid[MessageModifier[IO]].combineAll(List(pageDefaultsApplier))
      converters = new Converters[IO](
        UUIDGenerator.fromApplicativeError[IO],
        ReadableIdsResolver.RepoBasedResolver[IO](
          RepositoryK(repos.category, transactor.trans),
          RepositoryK(repos.model, transactor.trans)
        )
      )
      catalogService <- CatalogFs2Grpc.bindServiceResource[IO](
        CatalogImpl(services, modifier, converters)
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
