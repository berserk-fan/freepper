package ua.pomo.catalog.shared

import cats.effect.unsafe.IORuntime.global
import cats.effect.{IO, Resource}
import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

trait HasIOResource extends BeforeAndAfterAll with BeforeAndAfter { this: AnyFunSuite =>
  protected type Res
  protected def resource: Resource[IO, Res]
  protected val resourcePerTest = false

  private[shared] var resources: Res = _
  private var finalizers = IO.unit

  def testR(name: String)(f: Res => Any)(implicit pos: source.Position): Unit = {
    test(name)(f(resources))
  }

  private def init(): Unit = {
    val (t1, t2) = resource.allocated.unsafeRunSync()(global)
    resources = t1
    finalizers = t2
  }

  private def myFinalize(): Unit = {
    finalizers.unsafeRunSync()(global)
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
