package ua.pomo.catalog.infrastructure.persistance

import org.scalacheck.Gen
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.DbUnitTestSuite

import java.util.UUID
import ua.pomo.catalog.shared.Generators

class CategoryRepositoryImplTest extends DbUnitTestSuite with matchers.must.Matchers with ScalaCheckDrivenPropertyChecks {

  import CategoryRepositoryImpl._

  test("sqls") {
    val catuuid = CategoryUUID(UUID.randomUUID())
    val uuid: CategoryId = Left(catuuid)
    val readableId: CategoryId = Right(CategoryReadableId("some-id"))

    check(Queries.findCategory(uuid))
    check(Queries.findCategory(readableId))
    check(Queries.findCategories)
    check(Queries.deleteCategory(uuid))
    check(Queries.deleteCategory(readableId))

    val updateGen: Gen[UpdateCategory] = for {
      id <- Gen.oneOf(uuid, readableId)
      readableId <- Gen.option(Generators.Category.readableId)
      displayName <- Gen.option(Generators.Category.displayName)
      descr <- Gen.option(Generators.Category.description)
    } yield UpdateCategory(id, readableId, displayName, descr)

    forAll(updateGen) { update: UpdateCategory =>
      check(Queries.updateCategory(update))
    }

    forAll(Generators.Category.self) { cat: Category =>
      check(Queries.insertCategory(cat))
    }
  }
}
