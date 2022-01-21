package ua.pomo.catalog.app

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.app.Name._
import ua.pomo.catalog.domain.category.{CategoryId, CategoryReadableId}
import ua.pomo.catalog.domain.model.{ModelId, ModelReadableId}

class NameTest extends AnyFunSuite with EitherValues with Matchers {
  test("parser") {

    CategoryNameModule.of("categories/hello-world").value should equal(
      CategoryName(CategoryId(CategoryReadableId("hello-world")))
    )

    ModelNameModule.of("categories/hello-world/models/some-model").value should equal(
      ModelName(Some(CategoryId(CategoryReadableId("hello-world"))), ModelId(ModelReadableId("some-model")))
    )
  }

  test("materialiser") {
    val catName = "categories/hello-world"
    val modName = "categories/hello-world/models/model-name"
    val categoryName = CategoryNameModule.of(catName).value
    CategoryNameModule.toNameString(categoryName) should equal(catName)
    val modelName = ModelNameModule.of(modName).value
    ModelNameModule.toNameString(modelName) should equal(modName)
  }
}
