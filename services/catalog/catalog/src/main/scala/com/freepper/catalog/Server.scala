package com.freepper.catalog

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.kernel.Monoid
import com.freepper.catalog.api.CatalogFs2Grpc
import com.freepper.catalog.app.{CatalogImpl, Converters, ReadableIdsResolver, UUIDGenerator, programs}
import com.freepper.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier}
import com.freepper.catalog.domain.image.ImageDataRepository
import com.freepper.catalog.infrastructure.persistance.s3.S3ImageDataRepository
import doobie.ConnectionIO
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.typelevel.log4cats.LoggerFactory
import com.freepper.catalog.domain.RegistryHelper.implicits._
import com.freepper.catalog.domain.image.ImageDataRepository
import com.freepper.catalog.infrastructure.persistance.postgres
import com.freepper.common.TransactorHelpers
import com.freepper.common.app.programs.{CookieParser, GrpcMetadataParser, GrpcMetadataTransformer}
import com.freepper.common.domain.auth.{CallContext, CookieName}
import com.freepper.common.domain.crud.{Crud, Repository, Service}
import com.freepper.common.infrastracture.persistance.RepositoryK

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
      repos = postgres.postgresRepoRegistry
      services = programs.serviceRegistry[ConnectionIO, IO](
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
      cookieAuthExtractor <- Resource.eval(
        GrpcMetadataTransformer.cookieAuthExtractor[IO](config.auth.sessionCookieName)
      )
      catalogService <- CatalogFs2Grpc.serviceResource[IO, CallContext](
        CatalogImpl(services, modifier, converters),
        metadata =>
          cookieAuthExtractor.transform(metadata).flatMap {
            new GrpcMetadataParser[IO](config.auth).extractCallContext(_)
          }
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
