package ua.pomo.catalog.shared

import cats.effect.unsafe.IORuntime.global
import cats.effect.{IO, Resource}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll

trait HasIOResource extends BeforeAndAfterAll { this: AnyFunSuite =>
  protected type Res
  protected def resource: Resource[IO, Res]

  private[shared] var resources: Res = _
  private var finalizers = IO.unit

  override def beforeAll(): Unit = {
    val (t1, t2) = resource.allocated.unsafeRunSync()(global)
    resources = t1
    finalizers = t2
  }

  override def afterAll(): Unit = {
    finalizers.unsafeRunSync()(global)
  }

  def testR(name: String)(f: Res => Any): Unit = {
    test(name)(f(resources))
  }
}
