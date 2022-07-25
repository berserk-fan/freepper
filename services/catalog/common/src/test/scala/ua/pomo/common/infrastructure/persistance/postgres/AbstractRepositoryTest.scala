package ua.pomo.common.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import monocle.Lens
import ua.pomo.common.UnsafeRunnable.UnsafeRunnableSyntax
import ua.pomo.common.config.JdbcDatabaseConfig
import ua.pomo.common.{DbResources, DbUnitTestSuite, Resources}
import ua.pomo.common.domain.repository.{CrudOps, Repository}
import ua.pomo.common.infrastructure.persistance.postgres.AbstractRepositoryTest._

abstract class AbstractRepositoryTest[F[_], T <: TestCrud: CrudOps](
    repositoryR: DbResources => Resource[IO, Repository[IO, T]],
    generators: Generators[T],
    fixtureR: Resource[IO, T#Fixture],
    jdbcConfig: Resource[IO, JdbcDatabaseConfig],
    checkersUpdate: List[UpdateCheck[T, _]],
    checkersCreate: List[CreateCheck[T, _]]
) extends DbUnitTestSuite {
  case class TestResources(repository: Repository[IO, T], db: DbResources, fixture: T#Fixture)
  override type TestResource = TestResources
  override val resourcePerTest: Boolean = true
  override def getDbResources(resources: TestResources): DbResources = resources.db
  override protected def resource: Resource[IO, TestResource] =
    for {
      cfg <- jdbcConfig
      db <- Resources.dbTest(cfg)
      repo <- repositoryR(db)
      fixture <- fixtureR
    } yield TestResources(repo, db, fixture)

  testR("create delete") { case TestResources(repository, _, fixture) =>
    forAll(generators.create(fixture)) { create =>
      val id = repository.create(create).trRun()
      val found = repository.get(id).trRun()

      checkersCreate.foreach { case (lens1, lens2) =>
        lens1.get(create) should equal(lens2.get(found))
      }
      repository.delete(id).trRun()
    }
  }

  testR(s"update") { case TestResources(repository, _, fixture) =>
    forAll(generators.update(fixture)) { update =>
      repository.update(update).trRun()
      val afterUpdate = repository.get(CrudOps[T].getIdUpdate(update)).trRun()

      checkersUpdate.foreach { case (lens1, lens2) =>
        lens1.get(update).foreach(_ should equal(lens2.get(afterUpdate)))
      }
    }
  }
}

object AbstractRepositoryTest {
  protected type UpdateCheck[T <: TestCrud, U] = (Lens[T#Update, Option[U]], Lens[T#Entity, U])
  protected type CreateCheck[T <: TestCrud, U] = (Lens[T#Create, U], Lens[T#Entity, U])
}
