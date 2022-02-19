package ua.pomo.catalog

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.google.protobuf.field_mask.FieldMask
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import io.grpc.{Metadata, Status, StatusRuntimeException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{
  CatalogFs2Grpc,
  Category,
  CreateCategoryRequest,
  CreateImageListRequest,
  CreateModelRequest,
  DeleteCategoryRequest,
  DeleteImageListRequest,
  GetCategoryRequest,
  GetImageListRequest,
  Image,
  ImageList,
  Model,
  UpdateCategoryRequest,
  UpdateImageListRequest
}
import ua.pomo.catalog.domain.parameter.{ParamListDisplayName, ParameterList, ParameterListId}
import ua.pomo.catalog.shared.{HasIOResource, Resources}

import java.util.UUID

class CatalogImplIT extends AnyFunSuite with HasIOResource with Matchers {
  type Res = (CatalogFs2Grpc[IO, Metadata], Transactor[IO])
  override val resourcePerTest: Boolean = true
  override protected val resource: Resource[IO, Res] = for {
    config <- Resources.config
    jdbcConfig = config.jdbc.copy(schema = UUID.randomUUID().toString)
    appConfig = config.copy(jdbc = jdbcConfig)
    transactor <- Resources.transactor(jdbcConfig)
    _ <- Resources.schema(jdbcConfig, transactor)
    _ <- Server.resource(appConfig)
    client <- Resources.catalogClient(appConfig.server)
  } yield (client, transactor)

  private val category = Category(
    "",
    "",
    "some-category-id",
    "test category name",
    "descr"
  )

  //categories

  testR("create category") {
    case (client, _) =>
      val categoryReq = CreateCategoryRequest("categories", Some(category))
      val resp = client.createCategory(categoryReq, new Metadata()).unsafeRunSync()
      resp.copy(uid = "", name = "") should equal(category)
  }

  testR("get category") {
    case (client, _) =>
      val name =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync().name
      val getResp = client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
      getResp.copy(uid = "", name = "") should equal(category)

      val ex = intercept[StatusRuntimeException] {
        client.getCategory(GetCategoryRequest("bad name @!#!@#"), new Metadata()).unsafeRunSync()
      }
      ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("update category") {
    case (client, _) =>
      val crResp =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync()
      val newEntity = crResp.copy(readableId = "qwer", displayName = "qwera", description = "new qqww")
      val ex = intercept[StatusRuntimeException] {
        val update = UpdateCategoryRequest(Some(newEntity), None)
        client.updateCategory(update, new Metadata()).unsafeRunSync()
      }
      ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
      val fieldMask = FieldMask.of(Seq("readable_id", "display_name", "description"))
      val update = UpdateCategoryRequest(Some(newEntity), Some(fieldMask))
      val updated = client.updateCategory(update, new Metadata()).unsafeRunSync()
      updated should equal(newEntity)
      val getResp = client.getCategory(GetCategoryRequest(updated.name), new Metadata()).unsafeRunSync()
      getResp should equal(updated)
  }

  testR("delete category") {
    case (client, _) =>
      val name =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync().name
      client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
      client.deleteCategory(DeleteCategoryRequest(name), new Metadata()).unsafeRunSync()
      val ex = intercept[StatusRuntimeException] {
        client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
      }
      ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
  }

  //image lists

  private val imageList =
    ImageList("", "some name", Seq(Image("some-src", "some-alt"), Image("some-src2", "some-alt2")))

  testR("create image list") {
    case (client, _) =>
      val createResp = createImageList(client)
      createResp.copy(name = "") should equal(imageList)
      val getResp = client.getImageList(GetImageListRequest(createResp.name), new Metadata()).unsafeRunSync()
      getResp should equal(createResp)
  }

  testR("get image list") {
    case (client, _) =>
      val ex = intercept[StatusRuntimeException] {
        client.getImageList(GetImageListRequest(s"imageLists/${UUID.randomUUID()}"), new Metadata()).unsafeRunSync()
      }
      ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
      val createResp = createImageList(client)
      val getResp = client.getImageList(GetImageListRequest(createResp.name), new Metadata()).unsafeRunSync()
      createResp should equal(getResp)
  }

  testR("update image list") {
    case (client, _) =>
      val createResponse = createImageList(client)
      val newImageList =
        createResponse.copy(displayName = "abc",
                            images = List(Image("some-src-3", "some-alt-3"), Image("some-src-4", "some-alt-4")))
      val fieldMask = FieldMask(Seq("display_name", "images"))
      val updated = client
        .updateImageList(UpdateImageListRequest(Some(newImageList), Some(fieldMask)), new Metadata())
        .unsafeRunSync()
      updated should equal(newImageList)

      val newImages = List(Image("some-src-5", "some-alt-5"))
      //update images
      val newImageList2 = createResponse.copy(displayName = "qwer", images = newImages)
      val fieldMask2 = FieldMask(Seq("images"))
      val updated2 = client
        .updateImageList(UpdateImageListRequest(Some(newImageList2), Some(fieldMask2)), new Metadata())
        .unsafeRunSync()
      updated2.displayName should equal("abc")
      updated2.images should equal(newImages)
  }

  testR("delete image list") {
    case (client, _) =>
      val ex = intercept[StatusRuntimeException] {
        client
          .deleteImageList(DeleteImageListRequest(s"imageLists/${UUID.randomUUID().toString}"), new Metadata())
          .unsafeRunSync()
      }
      ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
      val createResponse = createImageList(client)
      client.deleteImageList(DeleteImageListRequest(createResponse.name), new Metadata()).unsafeRunSync()
      val ex2 = intercept[StatusRuntimeException] {
        client.getImageList(GetImageListRequest(createResponse.name), new Metadata()).unsafeRunSync()
      }
      ex2.getStatus.getCode should equal(Status.Code.NOT_FOUND)
  }

  private def createImageList(client: CatalogFs2Grpc[IO, Metadata]) = {
    client.createImageList(CreateImageListRequest("imageLists", Some(imageList)), new Metadata()).unsafeRunSync()
  }

  //models
  testR("create model") {
    case (client, xa) =>
      val category1 =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync()
      val imageList1 =
        client.createImageList(CreateImageListRequest("imageLists", Some(imageList)), new Metadata()).unsafeRunSync()
      val parameterListId1 = sql"""insert into parameter_lists (display_name) values ('')""".update
        .withUniqueGeneratedKeys[UUID]("id")
        .transact(xa)
        .unsafeRunSync()
      val parameterListId2 = sql"""insert into parameter_lists (display_name) values ('')""".update
        .withUniqueGeneratedKeys[UUID]("id")
        .transact(xa)
        .unsafeRunSync()

      val model = Model(
        "",
        "",
        "some-model",
        "some-display-name",
        "descr",
        Model.ImageList.ImageListName(imageList1.name),
        parameterLists = Model.ParameterListsOneof.ParameterListIds(
          Model.ParameterListIds(List(parameterListId1.toString, parameterListId2.toString)))
      )
      val createResp =
        client.createModel(CreateModelRequest(s"${category1.name}/models", Some(model)), new Metadata()).unsafeRunSync()
      val responseWithIgnoredFields =
        createResp.copy(name = "",
                        uid = "",
                        imageList = model.imageList,
                        parameterLists = model.parameterLists,
                        minimalPrice = None)
      responseWithIgnoredFields should equal(model)

      createResp.imageList.imageListData shouldBe defined
      createResp.imageList.imageListData.get should equal(imageList1)
      createResp.parameterLists.parameterListsData shouldBe defined
      createResp.parameterLists.parameterListsData.get.value.toList should equal(
        List(
          api.ParameterList(parameterListId1.toString, "", List()),
          api.ParameterList(parameterListId2.toString, "", List())
        ))
  }

  //products

}
