package ua.pomo.catalog

import cats.effect.{ IO, IOApp, Resource }
import doobie.Transactor
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import ua.pomo.catalog.api.{ CatalogFs2Grpc, CatalogImpl }

object Server extends IOApp.Simple {
  val catalogService: Resource[IO, ServerServiceDefinition] =
    CatalogFs2Grpc.bindServiceResource[IO](CatalogImpl[IO](null))

  override def run: IO[Nothing] = catalogService.use(run)

  def run(service: ServerServiceDefinition): IO[Nothing] =
    AppConfig
      .loadDefault[IO]
      .flatMap { config =>
        val _ = TransactorHelpers.fromConfig[IO](config.jdbc)
        NettyServerBuilder
          .forPort(config.server.serverPort)
          .addService(service)
          .resource[IO]
          .evalMap(server => IO(server.start()))
          .useForever
      }
}
