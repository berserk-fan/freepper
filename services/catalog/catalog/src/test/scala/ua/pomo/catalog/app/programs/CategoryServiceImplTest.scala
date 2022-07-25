package ua.pomo.catalog.app.programs

import cats.effect.{IO, Resource}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.infrastructure.persistance.postgres.CategoryRepositoryImpl
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Resources}

class CategoryServiceImplTest extends DbUnitTestSuite {
  case class TestResources(
      inmemory: CategoryService[IO],
      postgres: CategoryService[IO],
      db: DbResources,
      impls: Seq[(String, Impl)]
  )
  override type Impl = CategoryService[IO]
  override type TestResource = TestResources
  override def getDbResources(resources: TestResources): DbResources = resources.db
  override def getImpls(resources: TestResources): Seq[(String, CategoryService[IO])] = resources.impls
  override def names: Seq[String] = Seq("postgres", "inmem")
  override def resource: Resource[IO, TestResource] =
    for {
      db <- Resources.dbTest
      a1 <- Resource.eval(CategoryServiceImpl.makeInMemory[IO])
      a2 = CategoryServiceImpl(db.xa, CategoryRepositoryImpl())
    } yield TestResources(a1, a2, db, Seq(("postgres", a1), ("inmem", a2)))

  testEachImpl(s"description not empty") { impl =>
    val resp = impl
      .create(CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c")))
      .unsafeRunSync()

    resp.description.value should equal("c")
  }
}
