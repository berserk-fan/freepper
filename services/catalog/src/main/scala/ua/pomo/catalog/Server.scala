package ua.pomo.catalog

import cats.effect.{IO, IOApp}
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.ServerServiceDefinition
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import ua.pomo.catalog.app.CatalogImpl
import ua.pomo.catalog.app.programs.CategoryServiceImpl
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl

object Server extends IOApp.Simple {

  override def run: IO[Nothing] = AppConfig.loadDefault[IO].flatMap { config =>
    val transactor = TransactorHelpers.fromConfig[IO](config.jdbc)
    val categoryService = CategoryServiceImpl(transactor, CategoryRepositoryImpl())
    val catalogService = CatalogImpl.makeGrpc[IO](null, categoryService, null, null)
    catalogService.use(runGrpcServer(config.server.serverPort, _))
  }

  def runGrpcServer(port: Int, service: ServerServiceDefinition): IO[Nothing] =
    NettyServerBuilder
      .forPort(port)
      .addService(service)
      .addService(ProtoReflectionService.newInstance())
      .resource[IO]
      .evalMap(server => IO(server.start()))
      .useForever
}
