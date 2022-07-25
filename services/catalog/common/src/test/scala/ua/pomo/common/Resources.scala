package ua.pomo.common

import cats.effect.{IO, Resource}
import doobie.{Fragment, Transactor}
import doobie.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ua.pomo.common.config.JdbcDatabaseConfig

import java.util.UUID

object Resources {
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

  def dbTest(jdbc: JdbcDatabaseConfig): Resource[IO, DbResources] =
    for {
      config <- Resource.pure(jdbc.copy(schema = UUID.randomUUID().toString))
      transactor <- Resources.transactor(config)
      schema <- Resources.schema(config, transactor)
    } yield DbResources(transactor, schema)
}
