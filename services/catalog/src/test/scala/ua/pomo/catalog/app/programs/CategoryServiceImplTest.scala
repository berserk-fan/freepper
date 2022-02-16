package ua.pomo.catalog.app.programs

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Resource}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, HasDbResources, Resources}

class CategoryServiceImplTest extends DbUnitTestSuite {
  case class TestResources(inmemory: CategoryService[IO],
                           postgres: CategoryService[IO],
                           db: DbResources,
                           impls: Seq[Impl])
      extends HasDbResources
      with HasImpls
  override type Impl = CategoryService[IO]
  override type Res = TestResources
  override def names: Seq[String] = Seq("postgres", "impls")
  override def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      a1 <- Resource.eval(CategoryServiceImpl.makeInMemory[IO])
      a2 = CategoryServiceImpl(db.xa, CategoryRepositoryImpl())
    } yield TestResources(a1, a2, db, Seq(a1, a2))

  testEachImplR(s"description not empty") { (res, impl) =>
    implicit val runtime: IORuntime = res.db.runtime

    val resp = impl
      .create(CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c")))
      .unsafeRunSync()

    resp.description.value should equal("c")
  }
}
