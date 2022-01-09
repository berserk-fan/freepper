package ua.pomo.catalog

import cats.effect.{ IO, Resource }
import com.typesafe.scalalogging.StrictLogging
import io.grpc.{ Server, ServerBuilder, ServerServiceDefinition }
import ua.pomo.catalog.api.{ CatalogFs2Grpc, CatalogGrpc, CatalogImpl }
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all._

trait ServerStarter extends StrictLogging {
  private[this] var server: Option[Server] = None

  val helloService: Resource[IO, ServerServiceDefinition] =
    CatalogFs2Grpc.bindServiceResource[IO](new CatalogImpl())

  def run(service: ServerServiceDefinition) =
    NettyServerBuilder
      .forPort(9999)
      .addService(service)
      .resource[IO]
      .evalMap(server => IO(server.start()))
      .useForever

  helloService.use(run)

  protected def start(): IO[Unit] =
    for {
      config <- AppConfig.loadDefault[IO]
      catalogService = new CatalogImpl()
      server = Some(
        ServerBuilder
          .forPort(config.server.serverPort)
          .addService(CatalogGrpc.bindService(catalogService, ec))
          .asInstanceOf[ServerBuilder[_]]
          .build()
          .start()
      )
      _ = logger.info("Server started, listening on " + config.server.serverPort)
      _ = sys.addShutdownHook {
        System.err.println("*** shutting down gRPC server since JVM is shutting down")
        this.stop()
        System.err.println("*** server shut down")
      }
    } yield ()

  private def stop(): Unit = {
    server.foreach(_.shutdown())
  }

  protected def blockUntilShutdown(): Unit = {
    server.foreach(_.awaitTermination())
  }
}
