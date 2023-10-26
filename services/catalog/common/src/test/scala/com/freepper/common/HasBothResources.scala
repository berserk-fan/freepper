package com.freepper.common

import cats.effect.{MonadCancelThrow, Resource}
import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import scala.compiletime.uninitialized

trait HasBothResources[F[_]] extends BeforeAndAfterAll with BeforeAndAfter {
  this: AnyFunSuite =>
  protected def runResource[T <: Any](sr: SuiteResource, r: F[T]): T

  protected def runSuiteResource[T](r: F[T]): T

  def monadCancelThrow: MonadCancelThrow[F]

  protected type TestResource
  protected type SuiteResource

  protected def resource(suiteResource: SuiteResource): Resource[F, TestResource]

  protected def suiteResource: Resource[F, SuiteResource]

  private var resources: TestResource = uninitialized
  private var suiteResources: SuiteResource = uninitialized

  private var finalizers = monadCancelThrow.unit
  private var suiteFinalizers = monadCancelThrow.unit

  private def mySuiteFinalize(): Unit = {
    runSuiteResource(suiteFinalizers)
  }

  override def beforeAll(): Unit = {
    val (t1, t2) = runSuiteResource(suiteResource.allocated(monadCancelThrow))
    suiteResources = t1
    suiteFinalizers = t2
  }

  override def afterAll(): Unit = {
    mySuiteFinalize()
  }

  before {
    val (t1, t2) = runResource(suiteResources, resource(suiteResources).allocated(monadCancelThrow))
    resources = t1
    finalizers = t2
  }

  after {
    runResource(suiteResources, finalizers)
  }

  def testR(name: String)(f: (SuiteResource, TestResource) => Any)(implicit pos: source.Position): Unit = {
    test(name)(f(suiteResources, resources))
  }
}
