package ua.pomo.catalog.app

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image.ImageListId
import ua.pomo.catalog.domain.model.{ModelReadableId, ModelUUID}

import java.util.UUID

class ApiNameTest extends AnyFunSuite with EitherValues with Matchers {
  private val Uuid = UUID.randomUUID()
  private val Uuid2 = UUID.randomUUID()
  test("parser") {
    ApiName.category(s"categories/$Uuid").value should equal(
      CategoryName(CategoryUUID(Uuid))
    )

    ApiName.category(s"categories/$Uuid").value should equal(
      CategoryName(CategoryUUID(Uuid))
    )

    ApiName.models(s"categories/$Uuid/models").value should equal(
      ModelsName(Some(CategoryUUID(Uuid)))
    )

    ApiName.model(s"categories/$Uuid/models/$Uuid2").value should equal(
      ModelName(Some(CategoryUUID(Uuid)), ModelUUID(Uuid2))
    )

    ApiName.imageList(s"imageLists/$Uuid").value should equal(
      ImageListName(ImageListId(Uuid))
    )
  }

  test("materialiser") {
    val catName = s"categories/$Uuid"
    val modName = s"categories/$Uuid/models/$Uuid2"
    val imlName = s"imageLists/$Uuid"
    ApiName.category(catName).value.toNameString should equal(catName)
    ApiName.model(modName).value.toNameString should equal(modName)
    ApiName.imageList(imlName).value.toNameString should equal(imlName)
  }
}
