package ua.pomo.common.infrastructure.persistance.postgres

import cats.effect.{MonadCancelThrow, Resource}
import cats.MonadThrow
import cats.~>
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.monadError.catsSyntaxMonadError
import cats.syntax.functor.toFunctorOps
import org.scalacheck.{Gen, Test}
import org.scalacheck.effect.PropF
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import ua.pomo.common.{HasSuiteResource, ScalacheckEffectCheckers}
import ua.pomo.common.domain.repository.Crud
import org.scalactic.source
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.common.domain.{EntityTest, UnsafeRun}
import ua.pomo.common.domain.error.NotFound
import ua.pomo.common.infrastructure.persistance.postgres.RepositoryAbstractTest.TestContract

//
//  testEachImpl("delete count") { impl =>
//    impl.delete(ImageId(UUID.randomUUID())).trRun() should equal(0)
//    val id = impl.create(Generators.Image.create.sample.get).trRun()
//    val id2 = impl.create(Generators.Image.create.sample.get).trRun()
//    impl.delete(id).trRun() should equal(1)
//    impl.delete(id2).trRun() should equal(1)
//  }

abstract class RepositoryAbstractTest[F[_]: MonadThrow: LoggerFactory, G[_]: UnsafeRun: MonadCancelThrow, T <: Crud]()
    extends AnyFunSuite
    with Matchers
    with HasSuiteResource[G]
    with EitherValues
    with ScalacheckEffectCheckers {

  override protected type SuiteResource = (F ~> G, EntityTest[F, T])

  implicit override protected def scalaCheckTestParameters: Test.Parameters =
    Test.Parameters.default.withMinSuccessfulTests(50)

  override protected def genParameters: Gen.Parameters = Gen.Parameters.default

  protected def ignoredContracts: Set[RepositoryAbstractTest.TestContract] = Set()

  testContract(TestContract.CreateDelete, s"create delete contract") { case (_, et) =>
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

  testContract(TestContract.UpdateContract, s"update contract") { case (_, et) =>
    val gen = for {
      e <- et.generators.create
      u <- et.generators.update
    } yield (e, u)

    val p = PropF.forAllF(gen) { createAndUpdate: (T#Create, T#EntityId => T#Update) =>
      for {
        createdId <- et.repository.create(createAndUpdate._1)
        u = createAndUpdate._2(createdId)
        _ <- et.repository.update(u)
        afterUpdate <- et.repository.get(et.crudOps.getIdUpdate(u))
        _ = et.checkers.update(u, afterUpdate)
        _ <- et.repository.delete(createdId)
      } yield ()
    }
    checkProperty(p)
  }

  override protected def unsafeRun: UnsafeRun[G] = implicitly

  override protected def suiteResource: Resource[G, (F ~> G, EntityTest[F, T])]

  override protected def monadCancelThrow: MonadCancelThrow[G] = implicitly

  protected def testA(name: String)(f: SuiteResource => F[_])(implicit pos: source.Position): Unit = {
    testR(name)((s: SuiteResource) => unsafeRun.unsafeRunSync(s._1(f(s))))
  }

  private lazy val TestName: String = this.getClass.getSimpleName
  private def testContract[U](contract: TestContract, name: String)(
      f: SuiteResource => F[U]
  )(implicit pos: source.Position): Unit = {
    if (!ignoredContracts.contains(contract)) {
      val testWithLog = (s: SuiteResource) => {
        for {
          logger <- LoggerFactory[F].create
          _ <- logger.info(s"Started testing $contract test contract for $TestName")
          res <- f(s)
          _ <- logger.info(s"Finished testing $contract test contract for $TestName")
        } yield res
      }

      testA(name)(testWithLog)
    }
  }

  testContract(TestContract.GetReturnsNotFound, s"get on empty returns not found error") { case (_, et) =>
    val p = PropF.forAllF(et.generators.id) { id: T#EntityId =>
      et.repository
        .get(id)
        .redeemWith(
          err => MonadThrow[F].catchNonFatal(err shouldBe a[NotFound]),
          entity => MonadThrow[F].catchNonFatal[Assertion](fail(s"found entity $entity"))
        )
        .as(())
    }
    checkProperty(p)
  }

  //  testEachImpl("get failure should return NotFound") { impl =>
  //    intercept[NotFound] {
  //      impl.get(ImageId(UUID.randomUUID())).trRun()
  //    }
  //  }
}

object RepositoryAbstractTest {
  sealed trait TestContract
  object TestContract {
    case object CreateDelete extends TestContract
    case object UpdateContract extends TestContract
    case object GetReturnsNotFound extends TestContract
  }
}
