package ua.pomo.catalog.shared

import cats.effect.IO
import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite
import ua.pomo.common.HasResource

trait ForEachImpl { self: AnyFunSuite with HasResource[IO] =>
  type Impl
  def getImpls(resources: TestResource): Seq[(String, Impl)]
  def names: Seq[String]

  protected def testEachImpl(testName: String)(t: Impl => Any)(implicit pos: source.Position): Unit = {
    names.zipWithIndex.foreach { case (implName, idx) =>
      testR(s"$testName for $implName impl") { resources =>
        val (name, impl) = getImpls(resources)(idx)
        if (implName != name) {
          throw new IllegalArgumentException("mismatched 'names' and 'getImpls'")
        }
        t(impl)
      }
    }
  }

  protected def testEachImplR(testName: String)(t: (TestResource, Impl) => Any)(implicit pos: source.Position): Unit = {
    names.zipWithIndex.foreach { case (implName, idx) =>
      testR(s"$testName for $implName impl") { resources =>
        val (name, impl) = getImpls(resources)(idx)
        if (implName != name) {
          throw new IllegalArgumentException("mismatched 'names' and 'getImpls'")
        }
        t(resources, impl)
      }
    }
  }
}
