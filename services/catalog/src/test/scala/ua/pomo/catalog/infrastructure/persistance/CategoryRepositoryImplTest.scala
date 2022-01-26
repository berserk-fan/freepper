package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import org.scalatest.{AsyncTestSuite, EitherValues}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite with AsyncTestSuite with EitherValues {

  import CategoryRepositoryImpl._

  val postgres: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl()
  val inmemory: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl.makeInMemory[ConnectionIO].trRun()

  Seq(postgres, inmemory) foreach { impl =>
    test(s"queries ${impl.getClass.getSimpleName}") {
      val uuid = CategoryUUID(UUID.randomUUID())

      check(Queries.findCategory(uuid))
      check(Queries.findCategories)
      check(Queries.deleteCategory(uuid))

      forAll(Generators.Category.update) { update: UpdateCategory =>
        check(Queries.updateCategory(update))
      }

      forAll(Generators.Category.self) { cat: Category =>
        check(Queries.insertCategory(cat))
      }
    }

    test(s"api ${impl.getClass.getSimpleName}") {
      forAll(Generators.Category.self) { cat =>
        val found = impl.create(cat)
          .flatMap(impl.get)
          .trRun()

        found should equal(cat.copy(uuid = found.uuid))

        val dbId = found.uuid
        val rId = CategoryReadableId("some-id 2")
        val newDisplayName = CategoryDisplayName("qq2")
        impl.update(UpdateCategory(dbId, Some(rId), Some(newDisplayName), None)).trRun()
        impl.get(dbId).trRun().displayName.value should equal(newDisplayName)
        impl.delete(dbId).trRun()
        intercept[Exception] {
          impl.get(dbId).trRun()
        }
      }
    }
  }
}
