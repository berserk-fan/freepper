package ua.pomo.catalog

import cats.arrow.FunctionK
import cats.effect.unsafe.IORuntime
import cats.effect.{IO, MonadCancelThrow, Resource}
import cats.implicits.toTraverseOps
import com.google.protobuf.field_mask.FieldMask
import io.grpc.{Metadata, Status, StatusException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
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
import ua.pomo.catalog.app.programs.modifiers.PageDefaultsApplier
import ua.pomo.catalog.app.{CatalogImpl, Converters, ReadableIdsResolver, UUIDGenerator, programs}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.imageList.ImageListId
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.infrastructure.persistance.postgres._
import ua.pomo.catalog.infrastructure.persistance.s3.InMemoryImageDataRepository
import ua.pomo.catalog.shared.Generators
import ua.pomo.catalog.shared.Generators.ToLazyListOps
import ua.pomo.common.domain.crud.Service
import ua.pomo.common.domain.error.NotFound
import ua.pomo.common.{HasResource, TestIORuntime}

import java.util.UUID

class CatalogImplTest extends AnyFunSuite with HasResource[IO] with Matchers {

  private implicit val runtime: IORuntime = TestIORuntime.runtime
  override protected def runResource[T](r: IO[T]): T = r.unsafeRunSync()
  override def monadCancelThrow: MonadCancelThrow[IO] = implicitly
  case class TestResources(
      categoryService: Service[IO, ModelCrud],
      modelService: Service[IO, ModelCrud],
      service: CatalogFs2Grpc[IO, Metadata],
      converters: Converters[IO]
  )

  override protected type TestResource = TestResources

  override protected def resource: Resource[IO, TestResources] = {
    implicit val lf: LoggerFactory[IO] = Slf4jFactory[IO]
    val res = for {
      idr <- InMemoryImageDataRepository()
      repoReg <- inMemoryRepoRegistry[IO]
      services = programs.serviceRegistry[IO, IO](repoReg, FunctionK.id[IO], idr)
      defaultsApplier = PageDefaultsApplier[IO](config.defaultPageSize)
      converter = new Converters[IO](
        UUIDGenerator.fromApplicativeError[IO],
        ReadableIdsResolver.RepoBasedResolver(repoReg.category, repoReg.model)
      )
      catalogImpl = CatalogImpl[IO](services, defaultsApplier, converter)
    } yield TestResources(services.model, services.model, catalogImpl, converter)

    Resource.eval(res)
  }

  private val config = CatalogApiConfig(5)
  testR("list models") { case TestResources(_, modelService, impl, _) =>
    val categoryId = CategoryId(UUID.randomUUID())
    val totalModels = 10
    Generators.Model
      .createGen(ImageListId(UUID.randomUUID()), List.empty, Generators.Category.catId)
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .take(totalModels)
      .toList
      .traverse(modelService.create(_))
      .unsafeRunSync()

    val parent = ModelsName(Left(categoryId))

    val modelsCol = parent.toNameString
    noException should be thrownBy impl.listModels(ListModelsRequest(modelsCol, 0, ""), null).unsafeRunSync()
    val pageLength = 4
    val page1 = impl.listModels(ListModelsRequest(modelsCol, pageLength, ""), null).unsafeRunSync()
    page1.models.length should equal(pageLength)
    val page2 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page1.nextPageToken), null).unsafeRunSync()
    page2.models.length should equal(pageLength)
    val page3 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page2.nextPageToken), null).unsafeRunSync()
    page3.models.length should equal(2)
    impl.listModels(ListModelsRequest(modelsCol), null).unsafeRunSync().models.length should equal(
      config.defaultPageSize
    )

    val ex = intercept[StatusException] {
      impl.listModels(ListModelsRequest(modelsCol, -1), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("get model") { case TestResources(_, modelService, impl, _) =>
    val ex = intercept[StatusException] {
      val name =
        ModelName(Left(CategoryId(UUID.randomUUID())), Left(ModelId(UUID.randomUUID()))).toNameString
      impl.getModel(GetModelRequest(name), null).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)

    val model =
      modelService
        .create(
          Generators.Model.createGen(ImageListId(UUID.randomUUID()), List.empty, Generators.Category.catId).sample.get
        )
        .unsafeRunSync()
    noException should be thrownBy impl
      .getModel(GetModelRequest(ModelName(Left(model.categoryUid), Left(model.id)).toNameString), null)
      .unsafeRunSync()
  }

  testR("create model") { case TestResources(_, _, impl, _) =>
    val paramList = Seq(ParameterList(uid = UUID.randomUUID().toString))
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
      CreateModelRequest(ModelsName(Left(CategoryId(UUID.randomUUID()))).toNameString, Some(modelReq))
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
      CreateModelRequest(ModelsName(Left(CategoryId(UUID.randomUUID()))).toNameString, Some(modelReq2))
    noException should be thrownBy impl.createModel(req2, null).unsafeRunSync()
  }

  testR("delete model") { case TestResources(_, models, impl, _) =>
    DeleteModelRequest(
      ModelName(Left(CategoryId(UUID.randomUUID())), Left(ModelId(UUID.randomUUID()))).toNameString
    )
    val mod1 =
      models
        .create(
          Generators.Model.createGen(ImageListId(UUID.randomUUID()), List(), Generators.Category.catId).sample.get
        )
        .unsafeRunSync()
    noException should be thrownBy impl
      .deleteModel(DeleteModelRequest(ModelName(Left(mod1.categoryUid), Left(mod1.id)).toNameString), null)
      .unsafeRunSync()

    intercept[NotFound] {
      models.get(mod1.id).unsafeRunSync()
    }
  }

  testR("create category description not empty") { case TestResources(_, _, impl, _) =>
    val response = impl
      .createCategory(
        CreateCategoryRequest("categories", Some(api.Category(displayName = "a", description = "c", readableId = "d"))),
        null
      )
      .unsafeRunSync()

    response.description should equal("c")
  }

  testR("update model must support *") { case TestResources(_, modelService, impl, converter) =>
    val model =
      Generators.Model.createGen(ImageListId(UUID.randomUUID()), List(), Generators.Category.catId, None).sample.get
    val res = modelService.create(model).unsafeRunSync()
    val newDescr = ModelDescription("hello world")
    val updated = res.copy(description = newDescr)
    noException should be thrownBy impl
      .updateModel(
        UpdateModelRequest(Some(converter.toApi(updated).unsafeRunSync()), Some(FieldMask.of(Seq("*")))),
        null
      )
      .unsafeRunSync()
    modelService.get(updated.id).unsafeRunSync().description should equal(newDescr)
  }
}
