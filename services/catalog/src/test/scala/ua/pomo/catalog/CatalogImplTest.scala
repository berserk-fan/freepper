package ua.pomo.catalog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toTraverseOps
import io.grpc.{Metadata, Status, StatusException}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{CatalogFs2Grpc, CreateModelRequest, GetModelRequest, ListModelsRequest}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.app.programs.{CategoryServiceImpl, ModelServiceImpl}
import ua.pomo.catalog.app.{ApiName, CatalogImpl}
import ua.pomo.catalog.domain.category._
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
    val catalogImpl = CatalogImpl[IO](categoryService, modelService, config)
    (categoryService, modelService, catalogImpl)
  }

  test("list models") {
    val (_, modelService, impl) = makeImpls
    val categoryId = CategoryUUID(UUID.randomUUID())
    val totalModels = 10
    Generators.Model
      .createGen(ImageListId(UUID.randomUUID()))
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .take(totalModels)
      .toList
      .traverse(modelService.create)
      .unsafeRunSync()

    val parent = ModelsName(Some(categoryId))

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
      val name = ModelName(Some(CategoryUUID(UUID.randomUUID())), ModelId(UUID.randomUUID())).toNameString
      impl.getModel(GetModelRequest(name), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)

    val model = modelService.create(Generators.Model.createGen(ImageListId(UUID.randomUUID())).sample.get).unsafeRunSync()
    noException should be thrownBy impl
      .getModel(GetModelRequest(ModelName(Some(model.categoryId), model.uuid).toNameString), null)
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
      0,
      Some(api.ImageList(ImageListName(ImageListId(UUID.randomUUID())).toNameString))
    )

    val req = CreateModelRequest(ModelsName(Some(CategoryUUID(UUID.randomUUID()))).toNameString, Some(modelReq))
    val model = impl.createModel(req, null).unsafeRunSync()
    modelReq.copy(name = model.name, uuid = model.uuid) should equal(model)

    //ignore output_only field
    val modelReq2 = api.Model(
      "",
      "",
      "some-id",
      "some name",
      "some description",
      10,
      Some(api.ImageList(ImageListName(ImageListId(UUID.randomUUID())).toNameString))
    )
    val req2 = CreateModelRequest(ModelsName(Some(CategoryUUID(UUID.randomUUID()))).toNameString, Some(modelReq2))
    noException should be thrownBy impl.createModel(req2, null).unsafeRunSync()
  }
}
