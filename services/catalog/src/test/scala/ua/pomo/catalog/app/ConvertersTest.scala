package ua.pomo.catalog.app

import com.google.protobuf.field_mask.FieldMask
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{ListModelsRequest, UpdateCategoryRequest}
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.model.{ModelQuery, ModelSelector}

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import ua.pomo.catalog.api
import ua.pomo.catalog.app.ApiName.{CategoryName, CategoryRefId}

class ConvertersTest extends AnyFunSuite with Matchers with EitherValues {
  private val Uuid = UUID.randomUUID()
  private def encode(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(StandardCharsets.UTF_8))

  test("list models request") {
    val listModelsRequest = ListModelsRequest(s"categories/$Uuid/models", 10, "")
    Converters.toDomain(listModelsRequest).toEither.value should equal(
      ModelQuery(ModelSelector.CategoryIdIs(CategoryUUID(Uuid)), PageToken.NonEmpty(10, 0))
    )

    val listModelsRequest2 = ListModelsRequest(s"categories/$Uuid/models", 10, encode("""{"size": 10, "offset": 20}"""))
    Converters.toDomain(listModelsRequest2).toEither.value should equal(
      ModelQuery(ModelSelector.CategoryIdIs(CategoryUUID(Uuid)), PageToken.NonEmpty(10, 20))
    )
  }

  test("update category should get description") {
    val catId = CategoryUUID(UUID.randomUUID())
    val category = api.Category(CategoryName(CategoryRefId.Uid(catId)).toNameString,
                                catId.value.toString,
                                "some-id",
                                "somename",
                                "descr")
    val res =
      Converters.toDomain(UpdateCategoryRequest(Some(category), Some(FieldMask.of(Seq("description", "readable_id")))))
    res.description shouldBe defined
    res.displayName should equal(None)
    res.readableId shouldBe defined
  }
}
