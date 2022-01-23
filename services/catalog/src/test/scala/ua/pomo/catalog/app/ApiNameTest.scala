package ua.pomo.catalog.app

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.category.{CategoryId, CategoryReadableId}
import ua.pomo.catalog.domain.image.ImageListId
import ua.pomo.catalog.domain.model.{ModelId, ModelReadableId}

import java.util.UUID

class ApiNameTest extends AnyFunSuite with EitherValues with Matchers {
  val Uuid = UUID.randomUUID()
  test("parser") {
    ApiName.category("categories/hello-world").value should equal(
      CategoryName(CategoryId(CategoryReadableId("hello-world")))
    )

    ApiName.model("categories/hello-world/models/some-model").value should equal(
      ModelName(Some(CategoryId(CategoryReadableId("hello-world"))),
                ModelId(ModelReadableId("some-model")))
    )

    ApiName.imageList(s"imageLists/$Uuid").value should equal(
      ImageListName(ImageListId(Uuid))
    )
  }

  test("materialiser") {
    val catName = "categories/hello-world"
    val modName = "categories/hello-world/models/model-name"
    val imlName = s"imageLists/$Uuid"
    ApiName.category(catName).value.toNameString should equal(catName)
    ApiName.model(modName).value.toNameString should equal(modName)
    ApiName.imageList(imlName).value.toNameString should equal(imlName)
  }
}
