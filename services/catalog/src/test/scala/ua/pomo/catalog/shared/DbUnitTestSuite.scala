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
    with EitherValues {
  override def transactor: doobie.Transactor[IO] = resources.db.xa
  implicit class ConnIOOps[T](t: ConnectionIO[T]) {
    def trRun(): T = t.transact(resources.db.xa).unsafeRunSync()(resources.db.runtime)
  }
  override type Res <: HasDbResources with HasImpls
}
