package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import doobie.implicits._
import org.scalacheck.Gen
import org.scalatest.AsyncTestSuite
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.DbUnitTestSuite

import java.util.UUID
import ua.pomo.catalog.shared.Generators

class CategoryRepositoryImplTest extends DbUnitTestSuite with AsyncTestSuite {

  import CategoryRepositoryImpl._

  val impl: CategoryRepository[ConnectionIO] = CategoryRepositoryImpl()

  test("queries") {
    val catuuid = CategoryUUID(UUID.randomUUID())
    val uuid: CategoryId = Left(catuuid)
    val readableId: CategoryId = Right(CategoryReadableId("some-id"))

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

  test("api") {
    forAll(Generators.Category.self) { cat =>
      val found = impl.create(cat)
        .flatMap { insertId => impl.get(Left(insertId)) }
        .trRun()

      found should equal(cat.copy(id = found.id))
  
      val dbId = found.id
      val rId = CategoryReadableId("some_id_2")
      val newDisplayName = CategoryDisplayName("qq2")
      impl.update(UpdateCategory(Left(dbId), Some(rId), Some(newDisplayName), None)).trRun()
      impl.get(Right(rId)).trRun().displayName.value should equal(newDisplayName)
      impl.delete(Left(dbId)).trRun()
      intercept[Exception] {
        impl.get(Left(dbId)).trRun()
      }
    }
  }
}
