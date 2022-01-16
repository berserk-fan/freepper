package ua.pomo.catalog

import cats.effect.IOApp
import cats.effect.{ ExitCode, IO }
import com.typesafe.scalalogging.LazyLogging
import ua.pomo.catalog.infrastructure.DBMigrations

object DBMigrationsCommand extends IOApp with LazyLogging {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _   <- IO(logger.info(s"Migrating database configuration"))
      cfg <- AppConfig.loadDefault[IO]
      _   <- DBMigrations.migrate[IO](cfg.jdbc)
    } yield ExitCode.Success
}
