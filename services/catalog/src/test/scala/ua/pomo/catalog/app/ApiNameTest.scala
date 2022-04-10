package ua.pomo.catalog.app

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image.ImageListId
import ua.pomo.catalog.domain.model.ModelId
import ua.pomo.catalog.domain.product.ProductId

import java.util.UUID

class ApiNameTest extends AnyFunSuite with EitherValues with Matchers {
  private val Uuid = UUID.randomUUID()
  private val Uuid2 = UUID.randomUUID()
  private val Uuid3 = UUID.randomUUID()

  test("parser") {
    ApiName.category(s"categories/$Uuid").value should equal(
      CategoryName(CategoryId(Uuid))
    )

    ApiName.category(s"categories/$Uuid").value should equal(
      CategoryName(CategoryId(Uuid))
    )

    ApiName.models(s"categories/$Uuid/models").value should equal(
      ModelsName(CategoryId(Uuid))
    )

    ApiName.model(s"categories/$Uuid/models/$Uuid2").value should equal(
      ModelName(CategoryId(Uuid), ModelId(Uuid2))
    )

    ApiName.imageList(s"imageLists/$Uuid").value should equal(
      ImageListName(ImageListId(Uuid))
    )

    ApiName.products(s"categories/$Uuid/models/$Uuid2/products").value should equal(
      ProductsName(CategoryId(Uuid), ModelId(Uuid2))
    )

    ApiName.product(s"categories/$Uuid/models/$Uuid2/products/$Uuid3").value should equal(
      ProductName(CategoryId(Uuid), ModelId(Uuid2), ProductId(Uuid3))
    )
  }

  test("materialiser") {
    val catName = s"categories/$Uuid"
    val modsName = s"categories/$Uuid/models"
    val modName = s"categories/$Uuid/models/$Uuid2"
    val productsName = s"categories/$Uuid/models/$Uuid2/products"
    val productName = s"categories/$Uuid/models/$Uuid2/products/$Uuid3"
    val imlName = s"imageLists/$Uuid"

    ApiName.category(catName).value.toNameString should equal(catName)
    ApiName.model(modName).value.toNameString should equal(modName)
    ApiName.imageList(imlName).value.toNameString should equal(imlName)
    ApiName.models(modsName).value.toNameString should equal(modsName)
    ApiName.products(productsName).value.toNameString should equal(productsName)
    ApiName.product(productName).value.toNameString should equal(productName)
  }
}
