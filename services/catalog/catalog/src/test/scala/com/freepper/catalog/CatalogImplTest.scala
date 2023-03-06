package com.freepper.catalog

import cats.arrow.FunctionK
import cats.effect.unsafe.IORuntime
import cats.effect.{IO, MonadCancelThrow, Resource}
import cats.implicits.toTraverseOps
import com.freepper.catalog.shared.Generators
import com.freepper.catalog.shared.Generators.{Category, Model}
import com.google.protobuf.field_mask.FieldMask
import io.grpc.{Metadata, Status, StatusException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import com.freepper.catalog.api.{CatalogFs2Grpc, CreateCategoryRequest, CreateModelRequest, DeleteModelRequest, GetModelRequest, ImageList, ListModelsRequest, Money, ParameterList, UpdateModelRequest}
import com.freepper.catalog.app.ApiName._
import com.freepper.catalog.app.programs.modifiers.PageDefaultsApplier
import com.freepper.catalog.app.{CatalogImpl, Converters, ReadableIdsResolver, UUIDGenerator, programs}
import com.freepper.catalog.domain.category._
import com.freepper.catalog.domain.imageList.ImageListId
import com.freepper.catalog.domain.model._
import com.freepper.catalog.infrastructure.persistance.postgres._
import com.freepper.catalog.infrastructure.persistance.s3.InMemoryImageDataRepository
import Generators.ToLazyListOps
import com.freepper.common.domain.crud.Service
import com.freepper.common.domain.error.NotFound
import com.freepper.common.{HasResource, TestIORuntime}
import com.freepper.catalog.domain.RegistryHelper.implicits._
import com.freepper.common.domain.auth.{CallContext, User, UserEmail, UserRole}

import java.util.UUID

class CatalogImplTest extends AnyFunSuite with HasResource[IO] with Matchers {

  private implicit val runtime: IORuntime = TestIORuntime.runtime
  override protected def runResource[T](r: IO[T]): T = r.unsafeRunSync()
  override def monadCancelThrow: MonadCancelThrow[IO] = implicitly
  case class TestResources(
      categoryService: Service[IO, ModelCrud],
      modelService: Service[IO, ModelCrud],
      service: CatalogFs2Grpc[IO, CallContext],
      converters: Converters[IO]
  )
  
  private val callContext = CallContext(Some(User(UserEmail("someemail@qq.com"), UserRole.Admin)))

  override protected type TestResource = TestResources

  override protected def resource: Resource[IO, TestResources] = {
    implicit val lf: LoggerFactory[IO] = Slf4jFactory[IO]
    val res = for {
      idr <- InMemoryImageDataRepository()
      repoReg <- inMemoryRepoRegistry[IO]
      basicServices = programs.basicServiceRegistry[IO, IO](repoReg, FunctionK.id[IO], idr)
      services = programs.serviceRegistry[IO, IO](repoReg, FunctionK.id[IO], idr)
      defaultsApplier = PageDefaultsApplier[IO](config.defaultPageSize)
      converter = new Converters[IO](
        UUIDGenerator.fromApplicativeError[IO],
        ReadableIdsResolver.RepoBasedResolver(repoReg.category, repoReg.model)
      )
      catalogImpl = CatalogImpl[IO](services, defaultsApplier, converter)
    } yield TestResources(basicServices.model, basicServices.model, catalogImpl, converter)

    Resource.eval(res)
  }

  private val config = CatalogApiConfig(5)
  testR("list models") { case TestResources(_, modelService, impl, _) =>
    val categoryId = CategoryId(UUID.randomUUID())
    val totalModels = 10
    Model
      .createGen(ImageListId(UUID.randomUUID()), List.empty, Category.catId)
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .take(totalModels)
      .toList
      .traverse(modelService.create(_))
      .unsafeRunSync()

    val parent = ModelsName(Left(categoryId))

    val modelsCol = parent.toNameString
    noException should be thrownBy impl.listModels(ListModelsRequest(modelsCol, 0, ""), callContext).unsafeRunSync()
    val pageLength = 4
    val page1 = impl.listModels(ListModelsRequest(modelsCol, pageLength, ""), callContext).unsafeRunSync()
    page1.models.length should equal(pageLength)
    val page2 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page1.nextPageToken), callContext).unsafeRunSync()
    page2.models.length should equal(pageLength)
    val page3 = impl.listModels(ListModelsRequest(modelsCol, pageLength, page2.nextPageToken), callContext).unsafeRunSync()
    page3.models.length should equal(2)
    impl.listModels(ListModelsRequest(modelsCol), callContext).unsafeRunSync().models.length should equal(
      config.defaultPageSize
    )

    val ex = intercept[StatusException] {
      impl.listModels(ListModelsRequest(modelsCol, -1), callContext).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("get model") { case TestResources(_, modelService, impl, _) =>
    val ex = intercept[StatusException] {
      val name =
        ModelName(Left(CategoryId(UUID.randomUUID())), Left(ModelId(UUID.randomUUID()))).toNameString
      impl.getModel(GetModelRequest(name), callContext).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)

    val model =
      modelService
        .create(
          Generators.Model.createGen(ImageListId(UUID.randomUUID()), List.empty, Generators.Category.catId).sample.get
        )
        .unsafeRunSync()
    noException should be thrownBy impl
      .getModel(GetModelRequest(ModelName(Left(model.categoryUid), Left(model.id)).toNameString), callContext)
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
    val model = impl.createModel(req, callContext).unsafeRunSync()
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
    noException should be thrownBy impl.createModel(req2, callContext).unsafeRunSync()
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
      .deleteModel(DeleteModelRequest(ModelName(Left(mod1.categoryUid), Left(mod1.id)).toNameString), callContext)
      .unsafeRunSync()

    intercept[NotFound] {
      models.get(mod1.id).unsafeRunSync()
    }
  }

  testR("create category description not empty") { case TestResources(_, _, impl, _) =>
    val response = impl
      .createCategory(
        CreateCategoryRequest("categories", Some(api.Category(displayName = "a", description = "c", readableId = "d"))),
        callContext
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
        callContext
      )
      .unsafeRunSync()
    modelService.get(updated.id).unsafeRunSync().description should equal(newDescr)
  }
}
