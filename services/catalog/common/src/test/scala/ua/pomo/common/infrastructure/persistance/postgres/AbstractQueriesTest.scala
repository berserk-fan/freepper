package ua.pomo.common.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import ua.pomo.common.config.JdbcDatabaseConfig
import ua.pomo.common.{DbResources, DbUnitTestSuite, Resources}
import ua.pomo.common.infrastracture.persistance.postgres.Queries

abstract class AbstractQueriesTest[F[_], T <: TestCrud](
    queries: Queries[T],
    generators: Generators[T],
    fixture: Resource[IO, T#Fixture],
    jdbcConfig: Resource[IO, JdbcDatabaseConfig]
) extends DbUnitTestSuite {
  case class TestResources(db: DbResources, fixture: T#Fixture)
  override type TestResource = TestResources
  override val resourcePerTest: Boolean = false
  override def getDbResources(resources: TestResources): DbResources = resources.db
  override protected def resource: Resource[IO, TestResource] =
    for {
      cfg <- jdbcConfig
      db <- Resources.dbTest(cfg)
      fix <- fixture
    } yield TestResources(db, fix)

  testR("queries") { case TestResources(_, fixture) =>
    forAll(generators.query(fixture)) { query =>
      check(queries.find(query))
    }
    forAll(generators.id) { id =>
      check(queries.delete(id))
    }
    forAll(generators.create(fixture)) { create =>
      check(queries.create(create)._1)
    }
    forAll(generators.update(fixture)) { update =>
      check(queries.update(update))
    }
  }
}
