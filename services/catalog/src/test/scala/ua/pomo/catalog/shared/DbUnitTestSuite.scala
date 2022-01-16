package ua.pomo.catalog.shared

import cats.effect.IO
import doobie._
import doobie.implicits._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ua.pomo.catalog.infrastructure.DBMigrations
import ua.pomo.catalog.{AppConfig, JdbcDatabaseConfig, TransactorHelpers}

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal
import cats.effect.unsafe
import cats.effect.unsafe.IORuntime

import java.util.concurrent.Executors

object DbUnitTestSuite {
  private lazy val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  //https://github.com/typelevel/cats-effect-testing/blob/series/1.x/core/jvm/src/main/scala/cats/effect/testing/RuntimePlatform.scala
  private def createIORuntime(ec: ExecutionContext): unsafe.IORuntime = {
    val (blocking, blockingSD) = unsafe.IORuntime.createDefaultBlockingExecutionContext()
    val (scheduler, schedulerSD) = unsafe.IORuntime.createDefaultScheduler()
    unsafe.IORuntime(ec, blocking, scheduler, { () => blockingSD(); schedulerSD(); }, unsafe.IORuntimeConfig())
  }
  private lazy val ioRuntime: IORuntime = createIORuntime(DbUnitTestSuite.ec)
}

trait DbUnitTestSuite extends AnyFunSuite
  with doobie.scalatest.IOChecker
  with BeforeAndAfterAll
  with Matchers
  with ScalaCheckDrivenPropertyChecks {
  private implicit val logger: LogHandler = LogHandler.jdkLogHandler
  implicit class ConnIOOps[T](t: ConnectionIO[T]) {
    def trRun(): T = t.transact(xa).unsafeRunSync()(cats.effect.unsafe.implicits.global)
  }
  implicit lazy val ioRuntime: IORuntime = DbUnitTestSuite.ioRuntime
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
