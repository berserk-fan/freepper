package ua.pomo.common

import cats.effect.{MonadCancelThrow, Resource}
import org.scalactic.source
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

trait HasResource[F[_]] extends BeforeAndAfter {
  this: AnyFunSuite =>
  protected def runResource[T <: Any](r: F[T]): T

  def monadCancelThrow: MonadCancelThrow[F]

  protected type TestResource

  protected def resource: Resource[F, TestResource]

  private var resources: TestResource = _

  private var finalizers = monadCancelThrow.unit

  before {
    val (t1, t2) = runResource(resource.allocated(monadCancelThrow))
    resources = t1
    finalizers = t2
  }

  after {
    runResource(finalizers)
  }

  def testR(name: String)(f: TestResource => Any)(implicit pos: source.Position): Unit = {
    test(name)(f(resources))
  }
}
