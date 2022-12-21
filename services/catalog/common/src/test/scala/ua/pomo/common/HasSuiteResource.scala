package ua.pomo.common

import cats.effect.{MonadCancelThrow, Resource}
import org.scalactic.source
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import ua.pomo.common.domain.UnsafeRun

trait HasSuiteResource[F[_]] extends BeforeAndAfterAll {
  this: AnyFunSuite =>

  protected def monadCancelThrow: MonadCancelThrow[F]
  protected def unsafeRun: UnsafeRun[F]
 
  private implicit lazy val m: MonadCancelThrow[F] = monadCancelThrow
  unsafeRun

  protected type SuiteResource

  protected def suiteResource: Resource[F, SuiteResource]

  private var suiteResources: SuiteResource = _
  private var suiteFinalizers = monadCancelThrow.unit

  override def beforeAll(): Unit = {
    val (t1, t2) = unsafeRun.unsafeRunSync(suiteResource.allocated)
    suiteResources = t1
    suiteFinalizers = t2
  }

  override def afterAll(): Unit = {
    unsafeRun.unsafeRunSync(suiteFinalizers)
  }

  def testR(name: String)(f: SuiteResource => Any)(implicit pos: source.Position): Unit = {
    test(name)(f(suiteResources))
  }
}
