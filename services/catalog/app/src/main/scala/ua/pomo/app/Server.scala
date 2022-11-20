package ua.pomo.app

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ua.pomo.catalog

object Server extends IOApp.Simple {
  def prodServer: IO[Resource[IO, io.grpc.Server]] = {
    implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory[IO]

    for {
      logger <- LoggerFactory[IO].create
      _ <- logger.info("Starting up app. And yees I start catalog service and maybe other services... )))")
      config <- ConfigLoader.load[IO](Service.MainService)
      catalogConfig <- ConfigLoader.load[IO](Service.Catalog)
      catalogService <- catalog.Server.prodService(catalogConfig)
    } yield catalogService.flatMap { s =>
      NettyServerBuilder
        .forPort(config.serverPort)
        .addService(s)
        .addService(ProtoReflectionService.newInstance())
        .resource[IO]
        .evalMap(server => IO(server.start()))
    }
  }

  override def run: IO[Nothing] = {
    prodServer.flatMap(_.useForever)
  }
}
