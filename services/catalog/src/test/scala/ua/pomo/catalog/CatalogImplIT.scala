package ua.pomo.catalog

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.google.protobuf.field_mask.FieldMask
import io.grpc.{Metadata, Status, StatusRuntimeException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{
  CatalogFs2Grpc,
  Category,
  CreateCategoryRequest,
  CreateImageListRequest,
  DeleteCategoryRequest,
  GetCategoryRequest,
  GetImageListRequest,
  Image,
  ImageList,
  UpdateCategoryRequest
}
import ua.pomo.catalog.shared.{HasIOResource, Resources}

import java.util.UUID

class CatalogImplIT extends AnyFunSuite with HasIOResource with Matchers {
  type Res = CatalogFs2Grpc[IO, Metadata]
  override val resourcePerTest: Boolean = true
  override protected val resource: Resource[IO, Res] = for {
    config <- Resources.config
    jdbcConfig = config.jdbc.copy(schema = UUID.randomUUID().toString)
    appConfig = config.copy(jdbc = jdbcConfig)
    transactor <- Resources.transactor(jdbcConfig)
    _ <- Resources.schema(jdbcConfig, transactor)
    _ <- Server.resource(appConfig)
    client <- Resources.catalogClient(appConfig.server)
  } yield client

  private val category = Category(
    "",
    "",
    "some-category-id",
    "test category name",
    "descr"
  )

  //categories

  testR("create category") { client =>
    val categoryReq = CreateCategoryRequest("categories", Some(category))
    val resp = client.createCategory(categoryReq, new Metadata()).unsafeRunSync()
    resp.copy(id = "", name = "") should equal(category)
  }

  testR("get category") { client =>
    val name =
      client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync().name
    val getResp = client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
    getResp.copy(id = "", name = "") should equal(category)

    val ex = intercept[StatusRuntimeException] {
      client.getCategory(GetCategoryRequest("bad name @!#!@#"), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("update category") { client =>
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

  testR("delete category") { client =>
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

  testR("create image list") { client =>
    val imageList = ImageList("", "some name", Seq(Image("some-src", "some-alt"), Image("some-src2", "some-alt2")))
    val createResp =
      client.createImageList(CreateImageListRequest("imageLists", Some(imageList)), new Metadata()).unsafeRunSync()
    createResp.copy(name = "") should equal(imageList)
    val getResp = client.getImageList(GetImageListRequest(createResp.name), new Metadata()).unsafeRunSync()
    getResp should equal(createResp)
  }
  
  testR("get image list") { client => 
    fail()
  }

  //models

  //products
}
