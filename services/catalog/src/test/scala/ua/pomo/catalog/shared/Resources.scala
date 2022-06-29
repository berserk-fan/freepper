package ua.pomo.catalog.shared

import cats.effect.{IO, Resource}
import doobie.{Fragment, Transactor}
import ua.pomo.catalog.infrastructure.DBMigrations
import ua.pomo.catalog.{AppConfig, JdbcDatabaseConfig, ServerConfig, TransactorHelpers}
import doobie.implicits._
import fs2.grpc.syntax.all.fs2GrpcSyntaxManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.netty.NettyChannelBuilder
import ua.pomo.catalog.api.CatalogFs2Grpc

import java.util.UUID

object Resources {
  def config: Resource[IO, AppConfig] = {
    Resource.eval(AppConfig.loadDefault[IO])
  }

  def transactor(config: JdbcDatabaseConfig): Resource[IO, Transactor[IO]] = Resource.pure[IO, Transactor[IO]] {
    TransactorHelpers.fromConfig[IO](config)
  }

  /*allocate schema*/
  def schema(config: JdbcDatabaseConfig, transactor: Transactor[IO]): Resource[IO, Schema] = {
    Resource.make(
      DBMigrations
        .migrate[IO](config)
        .as(Schema())
    ) { _ =>
      sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(config.schema)}" CASCADE;""".update.run
        .transact(transactor)
        .as(())
    }
  }

  def catalogClient(config: ServerConfig): Resource[IO, CatalogFs2Grpc[IO, Metadata]] = {
    NettyChannelBuilder
      .forAddress("127.0.0.1", config.serverPort)
      .usePlaintext()
      .resource[IO]
      .flatMap {
        CatalogFs2Grpc.stubResource(_)
      }
  }

  def dbTest: Resource[IO, DbResources] =
    for {
      configInit <- Resources.config
      config = configInit.jdbc.copy(schema = UUID.randomUUID().toString)
      transactor <- Resources.transactor(config)
      schema <- Resources.schema(config, transactor)
    } yield DbResources(transactor, schema)
}
