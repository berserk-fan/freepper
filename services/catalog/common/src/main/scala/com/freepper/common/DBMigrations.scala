package com.freepper.common

import cats.effect.Sync
import cats.implicits.{toFlatMapOps, toFunctorOps}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.typelevel.log4cats.Logger
import com.freepper.common.config.JdbcDatabaseConfig

import scala.jdk.CollectionConverters.*
object DBMigrations {

  def migrate[F[_]: Sync: Logger](config: JdbcDatabaseConfig): F[Int] = for {
    _ <- Logger[F].info(
      "Running migrations from locations: " +
        config.migrationsLocations.mkString(", ")
    )
    res <- Sync[F].blocking { unsafeMigrate(config) }
    _ <- res._1.fold(Sync[F].unit)(Logger[F].error(_))
    _ <- Logger[F].info(s"Executed ${res._2} migrations")
  } yield res._2

  private def unsafeMigrate(config: JdbcDatabaseConfig): (Option[String], Int) = {
    val m: FluentConfiguration = Flyway.configure
      .dataSource(
        config.url,
        config.user,
        config.password
      )
      .group(true)
      .outOfOrder(false)
      .table(config.migrationsTable)
      .locations(
        config.migrationsLocations
          .map(new Location(_)) *
      )
      .baselineOnMigrate(true)
      .schemas(config.schema)
      .defaultSchema(config.schema)

    val errorLogsOpt = getErrorLogs(m)
    (errorLogsOpt, m.load().migrate().migrationsExecuted)
  }

  private def getErrorLogs(m: FluentConfiguration): Option[String] = {
    val validated = m
      .ignorePendingMigrations(true)
      .load()
      .validateWithResult()

    Option.when(!validated.validationSuccessful) {
      validated.invalidMigrations.asScala
        .map { error =>
          s"""
           |Failed validation:
           |  - version: ${error.version}
           |  - path: ${error.filepath}
           |  - description: ${error.description}
           |  - errorCode: ${error.errorDetails.errorCode}
           |  - errorMessage: ${error.errorDetails.errorMessage}
        """.stripMargin.strip
        }
        .mkString("\n\n")
    }
  }
}
