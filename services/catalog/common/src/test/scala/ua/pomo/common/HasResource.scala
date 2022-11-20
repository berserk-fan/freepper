package ua.pomo.common

import cats.effect.{MonadCancelThrow, Resource}
import doobie.util.testing.UnsafeRun
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

trait HasResource[F[_]] extends BeforeAndAfterAll with BeforeAndAfter { this: AnyFunSuite =>
  def unsafeRun: UnsafeRun[F]
  def monadCancelThrow: MonadCancelThrow[F]

  protected type TestResource

  protected def resource: Resource[F, TestResource]
  protected val resourcePerTest = false

  protected var resources: TestResource = _
  private var finalizers = monadCancelThrow.unit

  private def init(): Unit = {
    val (t1, t2) = unsafeRun.unsafeRunSync(resource.allocated(monadCancelThrow))
    resources = t1
    finalizers = t2
  }

  private def myFinalize(): Unit = {
    unsafeRun.unsafeRunSync(finalizers)
  }

  override def beforeAll(): Unit = {
    if (!resourcePerTest) {
      init()
    }
  }

  override def afterAll(): Unit = {
    if (!resourcePerTest) {
      myFinalize()
    }
  }

  before {
    if (resourcePerTest) {
      init()
    }
  }

  after {
    if (resourcePerTest) {
      myFinalize()
    }
  }
}
