package ua.pomo.common.infrastructure.persistance.postgres

import cats.effect.{Resource, Sync}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import org.scalacheck.{Gen, Test}
import org.scalacheck.effect.PropF
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.common.{HasResource, ScalacheckEffectCheckers}
import ua.pomo.common.domain.{DbTestModule, EntityTest, RepoTestRegistry, RepositoryName}
import ua.pomo.common.domain.repository.Crud
import org.scalactic.source

abstract class AbstractDbModuleTest[F[_]: Sync](
    dbTestModuleR: Resource[F, DbTestModule[F]],
    repositoryNames: Seq[RepositoryName]
) extends AnyFunSuite
    with Matchers
    with HasResource[F]
    with EitherValues
    with ScalacheckEffectCheckers {

  implicit override protected def scalaCheckTestParameters: Test.Parameters =
    Test.Parameters.default.withMinSuccessfulTests(5)

  def testR(name: String)(f: TestResource => Any)(implicit pos: source.Position): Unit = {
    test(name)(f(resources))
  }

  def testRA(name: String)(f: TestResource => F[_])(implicit pos: source.Position): Unit = {
    val newF = (tr: TestResource) => {
      unsafeRun.unsafeRunSync(f(tr))
    }
    testR(name)(newF)
  }

  case class TestResources(repository: RepoTestRegistry[F])
  override type TestResource = TestResources
  override val resourcePerTest: Boolean = true

  override protected def genParameters: Gen.Parameters = Gen.Parameters.default

  override protected def resource: Resource[F, TestResource] = {
    for {
      dbTestModule <- dbTestModuleR
      repo = dbTestModule.dbTests
    } yield TestResources(repo)
  }

  def checkCreate[TT <: Crud](et: EntityTest[F, TT]): F[Unit] = {
    val p = PropF.forAllF(et.generators.create) { create =>
      for {
        found <- et.repository.create(create).flatMap(et.repository.get)
        _ = et.checkers.checkersCreate(create, found)
        _ <- et.repository.delete(et.co.getIdEntity(found))
      } yield ()
    }
    goodCheck(p)
  }

  def checkUpdate[TT <: Crud](et: EntityTest[F, TT]): F[Unit] = {
    val gen = for {
      e <- et.generators.create
      u <- et.generators.update
    } yield (e, u)

    val p = PropF.forAllF(gen) { createAndUpdate: (TT#Create, (TT#EntityId) => TT#Update) =>
      for {
        e <- et.repository.create(createAndUpdate._1)
        u = createAndUpdate._2(e)
        _ <- et.repository.update(u)
        afterUpdate <- et.repository.get(et.co.getIdUpdate(u))
        _ = et.checkers.checkersUpdate(u, afterUpdate)
      } yield ()
    }
    goodCheck(p)
  }

  repositoryNames.foreach { repoName =>
    testRA(s"create delete for ${repoName.value}") { case TestResources(registry) =>
      val entityTest = getEntityTest(repoName, registry)
      checkCreate(entityTest)
    }

    testRA(s"update for ${repoName.value}") { case TestResources(registry) =>
      val entityTest = getEntityTest(repoName, registry)
      checkUpdate(entityTest)
    }
  }

  private def getEntityTest(repoName: RepositoryName, registry: RepoTestRegistry[F]): EntityTest[F, _ <: Crud] = {
    registry.value.find(_.repositoryName == repoName).getOrElse(throw new RuntimeException(s"Not found entity test for ${repoName}"))
  }
}
