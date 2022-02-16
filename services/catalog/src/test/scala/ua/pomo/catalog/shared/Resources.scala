package ua.pomo.catalog.shared

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Resource, unsafe}
import doobie.{Fragment, Transactor}
import ua.pomo.catalog.infrastructure.DBMigrations
import ua.pomo.catalog.{AppConfig, JdbcDatabaseConfig, TransactorHelpers}
import doobie.implicits._
import fs2.grpc.syntax.all.fs2GrpcSyntaxManagedChannelBuilder
import io.grpc.{ManagedChannel, Metadata}
import io.grpc.netty.NettyChannelBuilder
import ua.pomo.catalog.api.CatalogFs2Grpc

import java.util.UUID
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object Resources {
  def config: Resource[IO, AppConfig] = {
    Resource.eval { AppConfig.loadDefault[IO] }
  }

  def transactor(config: JdbcDatabaseConfig): Resource[IO, Transactor[IO]] = Resource.pure[IO, Transactor[IO]] {
    TransactorHelpers.fromConfig[IO](config)
  }

  def schema(config: JdbcDatabaseConfig, transactor: Transactor[IO]): Resource[IO, Schema] = {
    Resource.make(
      DBMigrations
        .migrate[IO](config)
        .as(Schema())) { _ =>
      sql"""DROP schema IF EXISTS "${Fragment.const0(config.schema)}" CASCADE;""".update.run
        .transact(transactor)
        .as(())
    }
  }

  //https://github.com/typelevel/cats-effect-testing/blob/series/1.x/core/jvm/src/main/scala/cats/effect/testing/RuntimePlatform.scala
  def ioRuntime(): Resource[IO, IORuntime] = {
    Resource.eval {
      IO.blocking {
        val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
        val (blocking, blockingSD) = unsafe.IORuntime.createDefaultBlockingExecutionContext()
        val (scheduler, schedulerSD) = unsafe.IORuntime.createDefaultScheduler()
        unsafe.IORuntime(ec, blocking, scheduler, { () =>
          blockingSD(); schedulerSD();
        }, unsafe.IORuntimeConfig())
      }
    }
  }

  def catalogClient(port: Int): Resource[IO, CatalogFs2Grpc[IO, Metadata]] = {
    NettyChannelBuilder
      .forAddress("127.0.0.1", port)
      .resource[IO]
      .flatMap {
        CatalogFs2Grpc.stubResource(_)
      }
  }

  def dbTest: Resource[IO, DbResources] =
    for {
      ioRuntime <- Resources.ioRuntime()
      configInit <- Resources.config
      config = configInit.jdbc.copy(schema = UUID.randomUUID().toString)
      transactor <- Resources.transactor(config)
      schema <- Resources.schema(config, transactor)
    } yield DbResources(transactor, ioRuntime, schema)
}
