package ua.pomo.catalog.shared

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import ua.pomo.catalog.infrastructure.DBMigrations
import ua.pomo.catalog.{AppConfig, JdbcDatabaseConfig, TransactorHelpers}

import java.util.UUID
import scala.util.control.NonFatal

trait DbUnitTestSuite extends AnyFunSuite with doobie.scalatest.IOChecker with BeforeAndAfterAll {
  private implicit val logger: LogHandler = LogHandler.jdkLogHandler
  private val config: JdbcDatabaseConfig = AppConfig
    .loadDefault[IO]
    .unsafeRunSync()
    .jdbc
    .copy(schema = UUID.randomUUID().toString)

  val xa: Transactor[IO] = TransactorHelpers.fromConfig[IO](config)

  override def transactor: doobie.Transactor[IO] = xa

  override def beforeAll(): Unit = {
    try {
      runMigration()
    } catch {
      case NonFatal(e) =>
        cleanUp()
        throw e
    }
  }

  private def runMigration(): Unit = {
    DBMigrations
      .migrate[IO](config)
      .unsafeRunSync()
    ()
  }

  override def afterAll(): Unit = cleanUp()

  private def cleanUp(): Unit = {
    sql"""
        DROP schema IF EXISTS "${Fragment.const0(config.schema)}" CASCADE;
    """.update.run
      .transact(xa)
      .unsafeRunSync()
    ()
  }
}
