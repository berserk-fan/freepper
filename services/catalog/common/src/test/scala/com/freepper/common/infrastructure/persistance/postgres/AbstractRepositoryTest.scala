package com.freepper.common.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.effect.{MonadCancelThrow, Resource}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.syntax.monadError.catsSyntaxMonadError
import com.freepper.common.ScalacheckEffectCheckers
import com.freepper.common.domain.{EntityTest, UnsafeRun}
import org.scalacheck.effect.PropF
import org.scalacheck.{Gen, Test}
import org.scalactic.source
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, EitherValues}
import org.typelevel.log4cats.LoggerFactory
import com.freepper.common.domain.crud.Crud
import com.freepper.common.domain.error.NotFound
import com.freepper.common.domain.{EntityTest, UnsafeRun}
import com.freepper.common.infrastructure.persistance.postgres.AbstractRepositoryTest.TestContract
import com.freepper.common.{HasSuiteResource, ScalacheckEffectCheckers}

import scala.language.adhocExtensions

import Crud.*

abstract class AbstractRepositoryTest[F[_]: MonadThrow: LoggerFactory, G[_]: UnsafeRun: MonadCancelThrow, C[_]]()(
    implicit updateId: monocle.Getter[C[Update], C[EntityId]]
) extends AnyFunSuite
    with Matchers
    with HasSuiteResource[G]
    with EitherValues
    with ScalacheckEffectCheckers {

  testContract(TestContract.CreateDeleteContract, s"create delete contract") { et =>
    val p = PropF.forAllF(et.generators.create) { create =>
      for {
        createdId <- et.repository.create(create)
        found <- et.repository.get(createdId)
        _ = et.checkers.create(create, found)
        _ <- et.repository.delete(createdId)
      } yield ()
    }
    checkProperty(p)
  }

  testContract(TestContract.UpdateContract, s"update contract") { et =>
    val gen = for {
      e <- et.generators.create
      u <- et.generators.update
    } yield (e, u)

    val p = PropF.forAllF(gen) { (createAndUpdate: (C[Create], C[EntityId] => C[Update])) =>
      {
        for {
          createdId <- et.repository.create(createAndUpdate._1)
          u = createAndUpdate._2(createdId)
          _ <- et.repository.update(u)
          afterUpdate <- et.repository.get(updateId.get(u))
          _ = et.checkers.update(u, afterUpdate)
          _ <- et.repository.delete(createdId)
        } yield ()
      }
    }
    checkProperty(p)
  }

  testContract(TestContract.GetReturnsNotFoundContract, s"get on empty returns not found error") { et =>
    val p = PropF.forAllF(et.generators.id) { (id: C[EntityId]) =>
      {
        et.repository
          .get(id)
          .redeemWith(
            err => MonadThrow[F].catchNonFatal(err.shouldBe(a[NotFound])),
            entity => MonadThrow[F].catchNonFatal[Assertion](fail(s"found entity $entity"))
          )
          .as(())
      }
    }
    checkProperty(p)
  }

  override protected type SuiteResource = EntityTest[F, G, C]

  protected def minSuccessfulTests: Int = 50

  implicit override protected def scalaCheckTestParameters: Test.Parameters =
    Test.Parameters.default.withMinSuccessfulTests(minSuccessfulTests)

  override protected def genParameters: Gen.Parameters = Gen.Parameters.default

  protected def ignoredContracts: Set[AbstractRepositoryTest.TestContract] = Set()

  override protected def unsafeRun: UnsafeRun[G] = implicitly

  override protected def suiteResource: Resource[G, EntityTest[F, G, C]]

  override protected def monadCancelThrow: MonadCancelThrow[G] = implicitly

  protected def testA[T](name: String)(f: SuiteResource => F[T])(implicit pos: source.Position): Unit = {
    testR(name)((s: SuiteResource) => unsafeRun.unsafeRunSync(s.runner(f(s))))
  }

  private lazy val TestName: String = this.getClass.getSimpleName
  private def testContract[U](contract: TestContract, name: String)(
      f: SuiteResource => F[U]
  )(implicit pos: source.Position): Unit = {
    if (!ignoredContracts.contains(contract)) {
      val testWithLog = (s: SuiteResource) => {
        for {
          logger <- LoggerFactory[F].create
          _ <- logger.info(s"Started testing $contract test contract of $TestName")
          res <- f(s)
          _ <- logger.info(s"Finished testing $contract test contract of $TestName")
        } yield res
      }

      testA(name)(testWithLog)
    }
  }
}

object AbstractRepositoryTest {
  sealed trait TestContract
  object TestContract {
    case object CreateDeleteContract extends TestContract
    case object UpdateContract extends TestContract
    case object GetReturnsNotFoundContract extends TestContract
  }
}
