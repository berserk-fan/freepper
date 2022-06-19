package ua.pomo.catalog.shared

import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite

trait ForEachImpl { self: AnyFunSuite with HasIOResource =>
  type Impl
  def getImpls(resources: TestResource): Seq[(String, Impl)]
  def names: Seq[String]

  protected def testEachImpl(testName: String)(t: Impl => Any)(implicit pos: source.Position): Unit = {
    names.zipWithIndex.foreach { case (implName, idx) =>
      test(s"$testName for $implName impl") {
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
      testR(s"$testName for $implName impl") { res =>
        val (name, impl) = getImpls(resources)(idx)
        if (implName != name) {
          throw new IllegalArgumentException("mismatched 'names' and 'getImpls'")
        }
        t(res, impl)
      }
    }
  }
}
