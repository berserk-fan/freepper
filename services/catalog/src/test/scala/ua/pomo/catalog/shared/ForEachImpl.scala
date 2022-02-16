package ua.pomo.catalog.shared

import org.scalatest.funsuite.AnyFunSuite

trait ForEachImpl extends HasIOResource { self: AnyFunSuite =>
  trait HasImpls {
    def impls: Seq[Impl]
  }

  type Impl
  def names: Seq[String]
  override type Res <: HasImpls

  protected def testEachImpl(testName: String)(t: Impl => Any): Unit = {
    names.zipWithIndex.foreach {
      case (implName, idx) =>
        test(s"$testName for $implName impl") {
          t(resources.impls(idx))
        }
    }
  }
  protected def testEachImplR(testName: String)(t: (Res, Impl) => Any): Unit = {
    names.zipWithIndex.foreach {
      case (implName, idx) =>
        testR(s"$testName for $implName impl") { res =>
          t(res, resources.impls(idx))
        }
    }
  }
}
