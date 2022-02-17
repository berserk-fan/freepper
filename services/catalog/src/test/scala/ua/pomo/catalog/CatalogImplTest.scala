package ua.pomo.catalog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toTraverseOps
import io.grpc.{Metadata, Status, StatusException}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import squants.market.USD
import ua.pomo.catalog.api.{
  CatalogFs2Grpc,
  CreateCategoryRequest,
  CreateModelRequest,
  DeleteModelRequest,
  GetModelRequest,
  ListModelsRequest,
  Money
}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.app.programs.{CategoryServiceImpl, ImageListServiceImpl, ModelServiceImpl}
import ua.pomo.catalog.app.{ApiName, CatalogImpl}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image.ImageListId
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.Generators
import ua.pomo.catalog.shared.Generators.ToLazyListOps

import java.util.UUID

class CatalogImplTest extends AnyFunSuite with BeforeAndAfter with Matchers {
  private val config = CatalogApiConfig(5)
  def makeImpls: (CategoryService[IO], ModelService[IO], CatalogFs2Grpc[IO, Metadata]) = {
    val categoryService = CategoryServiceImpl.makeInMemory[IO].unsafeRunSync()
    val modelService = ModelServiceImpl.makeInMemory[IO].unsafeRunSync()
    val imageListService = ImageListServiceImpl.makeInMemory[IO]
    val catalogImpl = CatalogImpl[IO](null, categoryService, modelService, imageListService, config)
    (categoryService, modelService, catalogImpl)
  }

  test("list models") {
    val (_, modelService, impl) = makeImpls
    val categoryId = CategoryUUID(UUID.randomUUID())
    val totalModels = 10
    Generators.Model
      .createGen(ImageListId(UUID.randomUUID()), List.empty)
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .take(totalModels)
      .toList
      .traverse(modelService.create)
      .unsafeRunSync()

    val parent = ModelsName(categoryId)

    val modelsCol = parent.toNameString
    noException should be thrownBy impl.listModels(ListModelsRequest(modelsCol, 0, ""), null).unsafeRunSync()
    val pageLength = 4
    val page1 = impl.listModels(ListModelsRequest(modelsCol, pageLength, ""), null).unsafeRunSync()
    page1.models.length should equal(pageLength)
    val page2 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page1.nextPageToken), null).unsafeRunSync()
    page2.models.length should equal(pageLength)
    val page3 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page2.nextPageToken), null).unsafeRunSync()
    page3.models.length should equal(2)
    impl.listModels(ListModelsRequest(modelsCol, 0, ""), null).unsafeRunSync().models.length should equal(
      config.defaultPageSize)

    val ex = intercept[StatusException] {
      impl.listModels(ListModelsRequest(modelsCol, -1), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  test("get model") {
    val (_, modelService, impl) = makeImpls
    val ex = intercept[StatusException] {
      val name = ModelName(CategoryUUID(UUID.randomUUID()), ModelId(UUID.randomUUID())).toNameString
      impl.getModel(GetModelRequest(name), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)

    val model =
      modelService
        .create(Generators.Model.createGen(ImageListId(UUID.randomUUID()), List.empty).sample.get)
        .unsafeRunSync()
    noException should be thrownBy impl
      .getModel(GetModelRequest(ModelName(model.categoryId, model.id).toNameString), null)
      .unsafeRunSync()
  }

  test("create model") {
    val (_, _, impl) = makeImpls
    val modelReq = api.Model(
      "",
      "",
      "some-id",
      "some name",
      "some description",
      Some(api.ImageList(ImageListName(ImageListId(UUID.randomUUID())).toNameString)),
      Some(Money(USD.code, 0)),
    )

    val req = CreateModelRequest(ModelsName(CategoryUUID(UUID.randomUUID())).toNameString, Some(modelReq))
    val model = impl.createModel(req, null).unsafeRunSync()
    modelReq.copy(name = model.name, uuid = model.uuid) should equal(model)

    //ignore output_only field
    val modelReq2 = api.Model(
      "",
      "",
      "some-id",
      "some name",
      "some description",
      Some(api.ImageList(ImageListName(ImageListId(UUID.randomUUID())).toNameString)),
      Some(Money(USD.code, 0))
    )
    val req2 = CreateModelRequest(ModelsName(CategoryUUID(UUID.randomUUID())).toNameString, Some(modelReq2))
    noException should be thrownBy impl.createModel(req2, null).unsafeRunSync()
  }

  test("delete model") {
    val (_, models, impl) = makeImpls
    val request =
      DeleteModelRequest(ModelName(CategoryUUID(UUID.randomUUID()), ModelId(UUID.randomUUID())).toNameString)
    val mod1 =
      models.create(Generators.Model.createGen(ImageListId(UUID.randomUUID()), List()).sample.get).unsafeRunSync()
    noException should be thrownBy impl
      .deleteModel(DeleteModelRequest(ModelName(mod1.categoryId, mod1.id).toNameString), null)
      .unsafeRunSync()

    intercept[NotFound] {
      models.get(mod1.id).unsafeRunSync()
    }
  }

  test("create category description not empty") {
    val (_, _, impl) = makeImpls
    val response = impl
      .createCategory(CreateCategoryRequest("categories",
                                            Some(api.Category(displayName = "a", name = "b", description = "c"))),
                      null)
      .unsafeRunSync()

    response.description should equal("c")
  }

}
