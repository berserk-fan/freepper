package ua.pomo.catalog.shared

import cats.effect.IO
import doobie._
import doobie.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.EitherValues

trait DbUnitTestSuite
    extends AnyFunSuite
    with doobie.scalatest.IOChecker
    with Matchers
    with ScalaCheckDrivenPropertyChecks
    with HasIOResource
    with ForEachImpl
    with EitherValues
    with HasIORuntime {
  def getDbResources(resources: TestResource): DbResources

  override def transactor: doobie.Transactor[IO] = getDbResources(resources).xa
  implicit class ConnIOOps[T](t: ConnectionIO[T]) {
    def trRun(): T = t.transact(getDbResources(resources).xa).unsafeRunSync()
  }
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 5)
}
