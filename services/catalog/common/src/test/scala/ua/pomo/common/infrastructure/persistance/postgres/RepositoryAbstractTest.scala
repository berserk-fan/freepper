package ua.pomo.common.infrastructure.persistance.postgres

import cats.effect.{MonadCancelThrow, Resource}
import cats.MonadThrow
import cats.~>
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import org.scalacheck.{Gen, Test}
import org.scalacheck.effect.PropF
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.common.{HasSuiteResource, ScalacheckEffectCheckers}
import ua.pomo.common.domain.repository.{Crud, CrudOps}
import org.scalactic.source
import ua.pomo.common.domain.{EntityTest, UnsafeRun}

abstract class RepositoryAbstractTest[F[_]: MonadThrow, G[_]: UnsafeRun: MonadCancelThrow, T <: Crud: CrudOps]()
    extends AnyFunSuite
    with Matchers
    with HasSuiteResource[G]
    with EitherValues
    with ScalacheckEffectCheckers {

  override protected type SuiteResource = (F ~> G, EntityTest[F, T])

  implicit override protected def scalaCheckTestParameters: Test.Parameters =
    Test.Parameters.default.withMinSuccessfulTests(5)

  override protected def genParameters: Gen.Parameters = Gen.Parameters.default

  testA(s"create delete contract") { case (_, et) =>
    val p = PropF.forAllF(et.generators.create) { create =>
      et.repository
        .create(create)
        .flatMap(et.repository.get)
        .map { found => et.checkers.create(create, found); found }
        .flatMap(found =>
          et.repository
            .delete(et.crudOps.getIdEntity(found))
            .map(_ => ())
        )
    }
    checkProperty(p)
  }

  testA(s"update contract}") { case (_, et) =>
    val gen = for {
      e <- et.generators.create
      u <- et.generators.update
    } yield (e, u)

    val p = PropF.forAllF(gen) { createAndUpdate: (T#Create, T#EntityId => T#Update) =>
      for {
        e <- et.repository.create(createAndUpdate._1)
        u = createAndUpdate._2(e)
        _ <- et.repository.update(u)
        afterUpdate <- et.repository.get(et.crudOps.getIdUpdate(u))
        _ = et.checkers.update(u, afterUpdate)
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
}
