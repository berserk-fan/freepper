package ua.pomo.catalog

import cats.effect.{IO, Resource}
import cats.effect.unsafe.implicits.global
import cats.implicits.toTraverseOps
import cats.kernel.Monoid
import com.google.protobuf.field_mask.FieldMask
import io.grpc.{Metadata, Status, StatusException}
import ua.pomo.catalog.api.{
  CatalogFs2Grpc,
  CreateCategoryRequest,
  CreateModelRequest,
  DeleteModelRequest,
  GetModelRequest,
  ImageList,
  ListModelsRequest,
  Money,
  ParameterList,
  UpdateModelRequest
}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.app.programs.{CategoryServiceImpl, ImageListServiceImpl, ModelServiceImpl, ProductServiceImpl}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.imageList.ImageListId
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter.ParameterListId
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}
import ua.pomo.catalog.shared.Generators.ToLazyListOps
import doobie.implicits._
import doobie.postgres.implicits._
import ua.pomo.catalog.app.{CatalogImpl, Converters}
import ua.pomo.catalog.app.programs.modifiers.{MessageModifier, PageDefaultsApplier, ReadableIdInNamesResolver}
import ua.pomo.catalog.infrastructure.persistance._

import java.util.UUID

class CatalogImplTest extends DbUnitTestSuite {
  override val resourcePerTest: Boolean = true
  override type Impl = Unit
  override val names: Seq[String] = Seq.empty
  case class TestResources(db: DbResources, impls: Seq[Impl]) extends HasDbResources with HasImpls
  override type Res = TestResources
  override def resource: Resource[IO, Res] = Resources.dbTest.map(db => TestResources(db, Seq.empty))

  private val config = CatalogApiConfig(5)
  def makeImpls: (CategoryService[IO], ModelService[IO], CatalogFs2Grpc[IO, Metadata]) = {
    val categoryService = CategoryServiceImpl.makeInMemory[IO].unsafeRunSync()
    val modelService = ModelServiceImpl.makeInMemory[IO].unsafeRunSync()
    val imageListService = ImageListServiceImpl.makeInMemory[IO]
    val productService = ProductServiceImpl.makeInMemory[IO].unsafeRunSync()
    val defaultsApplier = PageDefaultsApplier[IO](config.defaultPageSize)
    val catalogImpl =
      CatalogImpl[IO](productService, categoryService, modelService, imageListService, defaultsApplier)
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

    val parent = ModelsName(CategoryRefId.Uid(categoryId))

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
      config.defaultPageSize
    )

    val ex = intercept[StatusException] {
      impl.listModels(ListModelsRequest(modelsCol, -1), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  test("get model") {
    val (_, modelService, impl) = makeImpls
    val ex = intercept[StatusException] {
      val name = ModelName(CategoryRefId.Uid(CategoryUUID(UUID.randomUUID())), ModelId(UUID.randomUUID())).toNameString
      impl.getModel(GetModelRequest(name), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)

    val model =
      modelService
        .create(Generators.Model.createGen(ImageListId(UUID.randomUUID()), List.empty).sample.get)
        .unsafeRunSync()
    noException should be thrownBy impl
      .getModel(GetModelRequest(ModelName(CategoryRefId.Uid(model.categoryUid), model.id).toNameString), null)
      .unsafeRunSync()
  }

  test("create model") {
    val (_, _, impl) = makeImpls

    val id1 = sql"""insert into parameter_lists (display_name) values ('')""".update
      .withUniqueGeneratedKeys[ParameterListId]("id")
      .trRun()

    val paramList = Seq(ParameterList(uid = id1.toString))
    val imageList = Some(ImageList(name = ImageListName(ImageListId(UUID.randomUUID())).toNameString))
    val modelReq = api.Model(
      "",
      "",
      "some-id",
      "some name",
      "some description",
      imageList,
      Some(Money(0)),
      paramList
    )

    val req =
      CreateModelRequest(ModelsName(CategoryRefId.Uid(CategoryUUID(UUID.randomUUID()))).toNameString, Some(modelReq))
    val model = impl.createModel(req, null).unsafeRunSync()
    model.copy(parameterLists = paramList, imageList = imageList) should equal(
      modelReq.copy(name = model.name, uid = model.uid)
    )

    val modelReq2 = api.Model(
      "",
      "",
      "some-id",
      "some name",
      "some description",
      imageList,
      Some(Money(0)),
      paramList
    )
    val req2 =
      CreateModelRequest(ModelsName(CategoryRefId.Uid(CategoryUUID(UUID.randomUUID()))).toNameString, Some(modelReq2))
    noException should be thrownBy impl.createModel(req2, null).unsafeRunSync()
  }

  test("delete model") {
    val (_, models, impl) = makeImpls
    DeleteModelRequest(
      ModelName(CategoryRefId.Uid(CategoryUUID(UUID.randomUUID())), ModelId(UUID.randomUUID())).toNameString
    )
    val mod1 =
      models.create(Generators.Model.createGen(ImageListId(UUID.randomUUID()), List()).sample.get).unsafeRunSync()
    noException should be thrownBy impl
      .deleteModel(DeleteModelRequest(ModelName(CategoryRefId.Uid(mod1.categoryUid), mod1.id).toNameString), null)
      .unsafeRunSync()

    intercept[NotFound] {
      models.get(mod1.id).unsafeRunSync()
    }
  }

  test("create category description not empty") {
    val (_, _, impl) = makeImpls
    val response = impl
      .createCategory(
        CreateCategoryRequest("categories", Some(api.Category(displayName = "a", description = "c", readableId = "d"))),
        null
      )
      .unsafeRunSync()

    response.description should equal("c")
  }

  test("update model must support *") {
    val (_, modelService, impl) = makeImpls
    val model = Generators.Model.createGen(ImageListId(UUID.randomUUID()), List()).sample.get
    val res = modelService.create(model).unsafeRunSync()
    val newDescr = ModelDescription("hello world")
    val updated = res.copy(description = newDescr)
    noException should be thrownBy impl
      .updateModel(UpdateModelRequest(Some(Converters.toApi(updated)), Some(FieldMask.of(Seq("*")))), null)
      .unsafeRunSync()
    modelService.get(updated.id).unsafeRunSync().description should equal(newDescr)
  }

}
