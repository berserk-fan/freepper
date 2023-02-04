package ua.pomo.catalog.app

import com.google.protobuf.field_mask.FieldMask
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{ListModelsRequest, UpdateCategoryRequest}
import ua.pomo.catalog.app.ApiName.{CategoryName, CategoryRefId}
import ua.pomo.catalog.domain.category.CategoryId
import ua.pomo.catalog.domain.model.ModelSelector
import ua.pomo.common.{HasResource, TestIORuntime}
import ua.pomo.common.domain.crud.{PageToken, Query}
import ua.pomo.catalog.domain.RegistryHelper.implicits._

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import cats.effect.{IO, MonadCancelThrow, Resource}
import ua.pomo.catalog.infrastructure.persistance.postgres

class convertersTest extends AnyFunSuite with Matchers with EitherValues with HasResource[IO] with TestIORuntime {
  private val Uuid = UUID.randomUUID()

  override protected def runResource[T](r: IO[T]): T = r.unsafeRunSync()
  override def monadCancelThrow: MonadCancelThrow[IO] = implicitly

  override protected type TestResource = Converters[IO]

  override protected def resource: Resource[IO, Converters[IO]] = for {
    repos <- Resource.eval(postgres.inMemoryRepoRegistry[IO])
    converters = new Converters[IO](
      UUIDGenerator.fromApplicativeError,
      ReadableIdsResolver.RepoBasedResolver(repos.category, repos.model)
    )
  } yield converters

  private def encode(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(StandardCharsets.UTF_8))

  testR("list models request") { converters =>
    val listModelsRequest = ListModelsRequest(s"categories/$Uuid/models", 10, "")
    converters.toDomain(listModelsRequest).unsafeRunSync() should equal(
      Query(ModelSelector.CategoryIdIs(CategoryId(Uuid)), PageToken.NonEmpty(10, 0))
    )

    val listModelsRequest2 = ListModelsRequest(s"categories/$Uuid/models", 10, encode("""{"size": 10, "offset": 20}"""))
    converters.toDomain(listModelsRequest2).unsafeRunSync() should equal(
      Query(ModelSelector.CategoryIdIs(CategoryId(Uuid)), PageToken.NonEmpty(10, 20))
    )
  }

  testR("update category should get description") { converters =>
    val catId = CategoryId(UUID.randomUUID())
    val category = api.Category(
      CategoryName(Left(catId)).toNameString,
      catId.value.toString,
      "some-id",
      "somename",
      "descr"
    )
    val res =
      converters
        .toDomain(UpdateCategoryRequest(Some(category), Some(FieldMask.of(Seq("description", "readable_id")))))
        .unsafeRunSync()
    res.description shouldBe defined
    res.displayName should equal(None)
    res.readableId shouldBe defined
  }

  testR("field mask should support *") { converters =>
    val catId = CategoryId(UUID.randomUUID())
    val category = api.Category(
      CategoryName(Left(catId)).toNameString,
      catId.value.toString,
      "some-id",
      "somename",
      "descr"
    )
    val res = converters.toDomain(UpdateCategoryRequest(Some(category), Some(FieldMask.of(Seq("*"))))).unsafeRunSync()
    res.description shouldBe defined
    res.readableId shouldBe defined
  }
}
