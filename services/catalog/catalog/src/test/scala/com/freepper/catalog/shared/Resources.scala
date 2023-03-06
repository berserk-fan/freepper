package com.freepper.catalog.shared

import cats.effect.{IO, Resource}
import doobie.implicits._
import doobie.{Fragment, Transactor}
import fs2.grpc.syntax.all.fs2GrpcSyntaxManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.netty.NettyChannelBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig.generic.auto._
import com.freepper.catalog.api.CatalogFs2Grpc
import com.freepper.catalog.{AppConfig, ServerConfig}
import com.freepper.common.app.programs.GrpcMetadataParser
import com.freepper.common.config.JdbcDatabaseConfig
import com.freepper.common.domain.Schema
import com.freepper.common.domain.auth.{AuthConfig, CallContext}
import com.freepper.common.{AppConfigLoader, DBMigrations, DbResources, TransactorHelpers}

import java.util.UUID

object Resources {
  def config: Resource[IO, AppConfig] = {
    Resource.eval(AppConfigLoader.loadDefault[IO, AppConfig]("catalog"))
  }

  def transactor(config: JdbcDatabaseConfig): Resource[IO, Transactor[IO]] = Resource.pure[IO, Transactor[IO]] {
    TransactorHelpers.fromConfig[IO](config)
  }

  /*allocate schema*/
  def schema(config: JdbcDatabaseConfig, transactor: Transactor[IO]): Resource[IO, Schema] = {
    Resource.make(
      Slf4jFactory[IO].create.flatMap { logger =>
        implicit val l: Logger[IO] = logger
        DBMigrations
          .migrate[IO](config)
          .as(Schema())
      }
    ) { _ =>
      sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(config.schema)}" CASCADE;""".update.run
        .transact(transactor)
        .as(())
    }
  }

  def catalogClient(config: ServerConfig, authConfig: AuthConfig): Resource[IO, CatalogFs2Grpc[IO, CallContext]] = {
    NettyChannelBuilder
      .forAddress("127.0.0.1", config.serverPort)
      .usePlaintext()
      .resource[IO]
      .flatMap {
        CatalogFs2Grpc.mkClientResource(_, new GrpcMetadataParser[IO](authConfig).extractMetadata(_))
      }
  }

  def dbTest: Resource[IO, DbResources[IO]] =
    for {
      configInit <- Resources.config
      config = configInit.jdbc.copy(schema = UUID.randomUUID().toString)
      transactor <- Resources.transactor(config)
      schema1 <- Resources.schema(config, transactor)
    } yield new DbResources[IO] {
      override def xa: Transactor[IO] = transactor

      override def schema: Schema = schema1
    }
}
