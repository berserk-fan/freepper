package ua.pomo.catalog

import cats.effect.{ IO, IOApp, Resource }
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import ua.pomo.catalog.api.{ CatalogFs2Grpc, CatalogImpl }

object Server extends IOApp.Simple {
  val catalogService: Resource[IO, ServerServiceDefinition] =
    CatalogFs2Grpc.bindServiceResource[IO](new CatalogImpl[IO]())

  override def run: IO[Nothing] = catalogService.use(run)

  def run(service: ServerServiceDefinition): IO[Nothing] =
    NettyServerBuilder
      .forPort(9090)
      .addService(service)
      .resource[IO]
      .evalMap(server => IO(server.start()))
      .useForever
}
