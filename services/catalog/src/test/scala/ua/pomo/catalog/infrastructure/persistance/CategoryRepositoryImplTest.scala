package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import org.scalatest.EitherValues
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite with EitherValues {

  import CategoryRepositoryImpl._

  val postgres: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl()
  val inmemory: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl.makeInMemory[ConnectionIO].trRun()

  test(s"queries") {
    val uuid = CategoryUUID(UUID.randomUUID())

    check(Queries.findCategory(uuid))
    check(Queries.findCategories)
    check(Queries.deleteCategory(uuid))

    forAll(Generators.Category.update) { update: UpdateCategory =>
      check(Queries.updateCategory(update))
    }

    forAll(Generators.Category.create) { cat: CreateCategory =>
      check(Queries.insertCategory(cat))
    }
  }

  Seq(postgres, inmemory) foreach { impl =>
    test(s"api ${impl.getClass.getSimpleName}") {
      forAll(Generators.Category.create) { cat =>
        val found = impl
          .create(cat)
          .flatMap(impl.get)
          .trRun()

        found.displayName should equal(cat.displayName)
        found.description should equal(cat.description)
        found.readableId should equal(cat.readableId)

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

    test(s"description not empty ${impl.getClass.getSimpleName}") {
      val req = CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c"))
      val res = impl.create(req).trRun()
      impl.get(res).trRun().description.value should equal("c")
    }
  }
}
