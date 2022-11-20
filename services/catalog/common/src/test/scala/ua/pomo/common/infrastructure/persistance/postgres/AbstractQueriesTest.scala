//package ua.pomo.common.infrastructure.persistance.postgres
//
//import cats.effect.{Async, Resource}
//import doobie.scalatest.Checker
//import org.typelevel.log4cats.Logger
//import ua.pomo.common.domain.repository.Crud
//import ua.pomo.common.DbUnitTestSuite
//import ua.pomo.common.domain.Generators
//import ua.pomo.common.infrastracture.persistance.postgres.Queries
//
//abstract class AbstractQueriesTest[F[_]: Async: Logger, T <: Crud](
//    queries: Queries[T],
//    generatorsR: Resource[F, Generators[T]]
//) extends DbUnitTestSuite[F] with Checker[F] {
//  case class TestResources(fixture: Generators[T])
//  override type TestResource = TestResources
//  override val resourcePerTest: Boolean = false
//  override protected def resource: Resource[F, TestResource] =
//    for {
//      fix <- generatorsR
//    } yield TestResources(fix)
//
//  testR("queries") { case TestResources(generators) =>
//    forAll(generators.query) { query =>
//      check(queries.find(query))
//    }
//    forAll(generators.id) { id =>
//      check(queries.delete(id))
//    }
//    forAll(generators.create) { create =>
//      check(queries.create(create)._1)
//    }
//    forAll(generators.update) { update =>
//      check(queries.update(update))
//    }
//  }
//}
