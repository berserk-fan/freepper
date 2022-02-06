package ua.pomo.catalog.app.programs

import cats.effect.IO
import ua.pomo.catalog.domain.category.{
  CategoryDescription,
  CategoryDisplayName,
  CategoryReadableId,
  CategoryService,
  CategoryUUID,
  CreateCategory
}
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl
import ua.pomo.catalog.shared.DbUnitTestSuite

class CategoryServiceImplTest extends DbUnitTestSuite {
  def inmemory: CategoryService[IO] = CategoryServiceImpl.makeInMemory[IO].unsafeRunSync()
  def postgres: CategoryService[IO] = CategoryServiceImpl(xa, CategoryRepositoryImpl())

  Seq((inmemory, "inmemory"), (postgres, "postgres")).foreach {
    case (impl, name) =>
      test(s"description not empty $name") {
        val resp = impl
          .createCategory(CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c")))
          .unsafeRunSync()

        resp.description.value should equal("c")
      }
  }
}
