package ua.pomo.app

import cats.effect.{Async, ExitCode, IO, IOApp}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import ua.pomo.common.DBMigrations

object DBMigrationsCommand extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    logger <- Slf4jFactory[IO].create
    res <- {
      implicit val l: SelfAwareStructuredLogger[IO] = logger
      run1(args)
    }
  } yield res

  private def run1[F[_]: Logger: Async](args: List[String]): F[ExitCode] = for {
    _ <- Logger[F].info("Running migrations")
    serviceName <- args.headOption.fold(
      new IllegalArgumentException("Failed to run migration. args(0) should be service name.").raiseError[F, String]
    )(_.pure[F])
    res <- Service
      .fromName(serviceName)
      .fold(
        new IllegalArgumentException("Failed to run migration. args(0) should be service name.")
          .raiseError[F, ExitCode]
      )(service => runMigration[F].apply(service))
  } yield res

  private case class PartiallyAppliedRunMigration[F[_]]() {
    def apply[T <: Product](service: Service[T])(implicit e1: Logger[F], e2: Async[F]): F[ExitCode] = {
      for {
        cfg <- ConfigLoader.load[F](service)
        _ <- service
          .jdbcConfig(cfg)
          .fold(new IllegalArgumentException(s"Jdbc config is not avaliable for $service.").raiseError[F, Int])(
            DBMigrations.migrate[F](_)
          )
      } yield ExitCode.Success
    }
  }

  private def runMigration[F[_]]: PartiallyAppliedRunMigration[F] = PartiallyAppliedRunMigration[F]()
}
