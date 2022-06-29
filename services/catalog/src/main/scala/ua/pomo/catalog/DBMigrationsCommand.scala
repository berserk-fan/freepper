package ua.pomo.catalog

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.typelevel.log4cats.slf4j.{Slf4jFactory, loggerFactoryforSync}
import ua.pomo.catalog.infrastructure.DBMigrations

object DBMigrationsCommand extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      logger <- Slf4jFactory[IO].create
      _ <- IO(logger.info(s"Migrating database configuration"))
      cfg <- AppConfig.loadDefault[IO]
      _ <- DBMigrations.migrate[IO](cfg.jdbc)
    } yield ExitCode.Success
}
