package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import org.scalatest.{AsyncTestSuite, EitherValues}
import ua.pomo.catalog.domain.ReadableId
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite with AsyncTestSuite with EitherValues {

  import CategoryRepositoryImpl._

  val postgres: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl()
  val inmemory: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl.makeInMemory[ConnectionIO].trRun()

  Seq(postgres, inmemory) foreach { impl =>
    test(s"queries ${impl.getClass.getSimpleName}") {
      val catuuid = CategoryUUID(UUID.randomUUID())
      val uuid: CategoryId = CategoryId(catuuid)
      val readableId: CategoryId = CategoryId(CategoryReadableId(ReadableId.parse("some-id").value))

      check(Queries.findCategory(uuid))
      check(Queries.findCategory(readableId))
      check(Queries.findCategories)
      check(Queries.deleteCategory(uuid))
      check(Queries.deleteCategory(readableId))

      forAll(Generators.Category.update) { update: UpdateCategory =>
        check(Queries.updateCategory(update))
      }

      forAll(Generators.Category.self) { cat: Category =>
        check(Queries.insertCategory(cat))
      }
    }

    test(s"api${impl.getClass.getSimpleName}") {
      forAll(Generators.Category.self) { cat =>
        val found = impl.create(cat)
          .flatMap { insertId => impl.get(CategoryId(insertId)) }
          .trRun()

        found should equal(cat.copy(id = found.id))

        val dbId = found.id
        val rId = CategoryReadableId(ReadableId.parse("some_id_2").value)
        val newDisplayName = CategoryDisplayName("qq2")
        impl.update(UpdateCategory(CategoryId(dbId), Some(rId), Some(newDisplayName), None)).trRun()
        impl.get(CategoryId(rId)).trRun().displayName.value should equal(newDisplayName)
        impl.delete(CategoryId(dbId)).trRun()
        intercept[Exception] {
          impl.get(CategoryId(dbId)).trRun()
        }
      }
    }
  }
}
