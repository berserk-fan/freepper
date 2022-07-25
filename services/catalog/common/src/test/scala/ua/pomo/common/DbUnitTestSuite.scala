package ua.pomo.common

import cats.effect.IO
import doobie.ConnectionIO
import doobie.implicits._
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait UnsafeRunnable[F[_]] {
  def trRun[T](t: F[T]): T
}

object UnsafeRunnable {
  implicit class UnsafeRunnableSyntax[F[_], T](t: F[T]) {
    def trRun()(implicit e: UnsafeRunnable[F]): T = e.trRun(t)
  }

  implicit val unsafeRunnableIO: UnsafeRunnable[IO] = new UnsafeRunnable[IO] {
    override def trRun[T](t: IO[T]): T = t.unsafeRunSync()(TestRuntime.testRuntime)
  }
}

trait DbUnitTestSuite
    extends AnyFunSuite
    with doobie.scalatest.IOChecker
    with Matchers
    with ScalaCheckDrivenPropertyChecks
    with HasIOResource
    with EitherValues
    with HasIORuntime {
  protected def getDbResources(resources: TestResource): DbResources
  override def transactor: doobie.Transactor[IO] = getDbResources(resources).xa
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 5)
}
