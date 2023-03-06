package com.freepper.app

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import com.freepper
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Server extends IOApp.Simple {
  private def prodServer: Resource[IO, io.grpc.Server] = {
    implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory[IO]

    for {
      logger <- Resource.eval(LoggerFactory[IO].create)
      _ <- Resource.eval(
        logger.info("Starting up an app. And yees I start catalog service and maybe other services... )))")
      )
      config <- Resource.eval(ConfigLoader.load[IO](Service.MainService))
      catalogConfig <- Resource.eval(ConfigLoader.load[IO](Service.Catalog))
      catalogService <- freepper.catalog.Server.production.serviceResource(catalogConfig)
      res <- NettyServerBuilder
        .forPort(config.serverPort)
        .addService(catalogService)
        .addService(ProtoReflectionService.newInstance())
        .resource[IO]
        .evalMap(server => IO(server.start()))
    } yield res
  }

  override def run: IO[Nothing] = {
    prodServer.useForever
  }
}
