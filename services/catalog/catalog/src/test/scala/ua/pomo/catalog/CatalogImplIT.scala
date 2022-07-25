package ua.pomo.catalog

import cats.effect.{IO, Resource}
import cats.implicits.{catsSyntaxOptionId, toTraverseOps}
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{ConnectionIO, Transactor}
import io.grpc.{Metadata, Status, StatusRuntimeException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ua.pomo.catalog.api._
import ua.pomo.catalog.infrastructure.persistance.s3.InMemoryImageDataRepository
import ua.pomo.catalog.shared.Resources
import ua.pomo.common.{HasIOResource, TestRuntime}

import java.util.UUID
import scala.language.reflectiveCalls

class CatalogImplIT extends AnyFunSuite with HasIOResource with Matchers with HasIORuntime {
  type TestResource = (CatalogFs2Grpc[IO, Metadata], Transactor[IO])
  override val resourcePerTest: Boolean = true
  override protected def resource: Resource[IO, TestResource] = for {
    config <- Resources.config
    jdbcConfig = config.jdbc.copy(schema = UUID.randomUUID().toString)
    appConfig = config.copy(jdbc = jdbcConfig)
    transactor <- Resources.transactor(jdbcConfig)
    _ <- Resources.schema(jdbcConfig, transactor)
    _ <- Resource.suspend(
      Server.serverResource(appConfig, InMemoryImageDataRepository())(Slf4jFactory[IO])
    )
    client <- Resources.catalogClient(appConfig.server)
  } yield (client, transactor)

  private val category = Category(
    "",
    "",
    "some-category-id",
    "test category name",
    "descr"
  )

  private val imageData = getClass.getResourceAsStream("/kitty.webp").readAllBytes()
  private val image = Image(
    "",
    "",
    "some-folder/some-src",
    "some alt lalala",
    ByteString.copyFrom(imageData)
  )
  private val createImageRequest = CreateImageRequest("images", Some(image))

  // images
  testR("create image") { case (client, _) =>
    val resp = client.createImage(createImageRequest, new Metadata()).unsafeRunSync()
    resp.copy(uid = "", name = "") should equal(image.copy(data = ByteString.EMPTY))
  }

  testR("get image") { case (client, _) =>
    val name = client.createImage(createImageRequest, new Metadata()).unsafeRunSync().name
    val getResp = client.getImage(GetImageRequest(name), new Metadata()).unsafeRunSync()
    getResp.copy(uid = "", name = "") should equal(image.copy(data = ByteString.EMPTY))

    val ex = intercept[StatusRuntimeException] {
      client.getImage(GetImageRequest("bad name @!#!@#"), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("delete image") { case (client, _) =>
    val name = client.createImage(createImageRequest, new Metadata()).unsafeRunSync().name
    client.getImage(GetImageRequest(name), new Metadata()).unsafeRunSync()
    client.deleteImage(DeleteImageRequest(name), new Metadata()).unsafeRunSync()
    val ex = intercept[StatusRuntimeException] {
      client.getImage(GetImageRequest(name), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
  }

  testR("list images") { case (client, _) =>
    val name1 = client.createImage(createImageRequest, new Metadata()).unsafeRunSync().name
    val name2 = client.createImage(createImageRequest, new Metadata()).unsafeRunSync().name
    val name3 = client.createImage(createImageRequest, new Metadata()).unsafeRunSync().name

    val resp1 = client.listImages(ListImagesRequest("images", 2), new Metadata()).unsafeRunSync()
    resp1.images should have length 2
    val resp2 = client.listImages(ListImagesRequest("images", 2, resp1.nextPageToken), new Metadata()).unsafeRunSync()
    resp2.images should have length 1
    resp2.nextPageToken should equal("")
    (resp1.images ++ resp2.images).toSet.map((x: Image) => x.name) should equal(Set(name1, name2, name3))
  }

  // categories

  testR("create category") { case (client, _) =>
    val categoryReq = CreateCategoryRequest("categories", Some(category))
    val resp = client.createCategory(categoryReq, new Metadata()).unsafeRunSync()
    resp.copy(uid = "", name = "") should equal(category)
  }

  testR("get category") { case (client, _) =>
    val name =
      client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync().name
    val getResp = client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
    getResp.copy(uid = "", name = "") should equal(category)

    val ex = intercept[StatusRuntimeException] {
      client.getCategory(GetCategoryRequest("bad name @!#!@#"), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.INVALID_ARGUMENT)
  }

  testR("update category") { case (client, _) =>
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
    updated.copy(name = "") should equal(newEntity.copy(name = ""))
    val getResp = client.getCategory(GetCategoryRequest(updated.name), new Metadata()).unsafeRunSync()
    getResp should equal(updated)
  }

  testR("delete category") { case (client, _) =>
    val name =
      client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync().name
    client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
    client.deleteCategory(DeleteCategoryRequest(name), new Metadata()).unsafeRunSync()
    val ex = intercept[StatusRuntimeException] {
      client.getCategory(GetCategoryRequest(name), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
  }

  // image lists

  private val imageList =
    ImageList("", "", "some name", Seq(Image("", "some-src", "some-alt"), Image("", "some-src2", "some-alt2")))

  testR("create image list") { case (client, _) =>
    val createResp = createImageList(client)
    val getResp = client.getImageList(GetImageListRequest(createResp.name), new Metadata()).unsafeRunSync()
    getResp should equal(createResp)
  }

  testR("get image list") { case (client, _) =>
    val ex = intercept[StatusRuntimeException] {
      client.getImageList(GetImageListRequest(s"imageLists/${UUID.randomUUID()}"), new Metadata()).unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
    val createResp = createImageList(client)
    val getResp = client.getImageList(GetImageListRequest(createResp.name), new Metadata()).unsafeRunSync()
    createResp should equal(getResp)
  }

  testR("update image list") { case (client, _) =>
    val createResponse = createImageList(client)
    val newImageList =
      createResponse.copy(
        displayName = "abc",
        images = createResponse.images.take(1)
      )
    val fieldMask = FieldMask(Seq("display_name", "images"))
    val updated = client
      .updateImageList(UpdateImageListRequest(Some(newImageList), Some(fieldMask)), new Metadata())
      .unsafeRunSync()
    updated should equal(newImageList)

    val newImages2 = createResponse.images.drop(1)
    // update images
    val newImageList2 = createResponse.copy(displayName = "qwer", images = newImages2)
    val fieldMask2 = FieldMask(Seq("images"))
    val updated2 = client
      .updateImageList(UpdateImageListRequest(Some(newImageList2), Some(fieldMask2)), new Metadata())
      .unsafeRunSync()
    updated2.displayName should equal("abc")
    updated2.images should equal(newImages2)
  }

  testR("delete image list") { case (client, _) =>
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
    val images = imageList.images.map { image =>
      client.createImage(CreateImageRequest("images", Some(image)), new Metadata()).unsafeRunSync()
    }
    client
      .createImageList(CreateImageListRequest("imageLists", Some(imageList.copy(images = images))), new Metadata())
      .unsafeRunSync()
  }

  def modelFixture(client: CatalogFs2Grpc[IO, Metadata], xa: Transactor[IO]) =
    new {
      val category1 =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync()
      val imageList1 = createImageList(client)
      val image = api.Image("src", "alt")
      val parameterList1 = api.ParameterList(
        UUID.randomUUID().toString,
        "",
        List(
          api.Parameter(UUID.randomUUID().toString, "param-name-1", Some(image)),
          api.Parameter(UUID.randomUUID().toString, "param-name-2", Some(image)),
          api.Parameter(UUID.randomUUID().toString, "param-name-3", Some(image))
        )
      )
      val parameterList2 = api.ParameterList(UUID.randomUUID().toString, "", List())
      createParameterList(parameterList1).transact(xa).unsafeRunSync()
      createParameterList(parameterList2).transact(xa).unsafeRunSync()
      val modelParentName = s"${category1.name}/models"
      val model = Model(
        "",
        "",
        "some-model",
        "some-display-name",
        "descr",
        Some(imageList1),
        parameterLists = Seq(parameterList1, parameterList2)
      )
    }

  testR("create model") { case (client, xa) =>
    val f = modelFixture(client, xa)

    val createResp =
      client
        .createModel(CreateModelRequest(f.modelParentName, Some(f.model)), new Metadata())
        .unsafeRunSync()
    val responseWithIgnoredFields =
      createResp.copy(
        name = "",
        uid = "",
        imageList = f.model.imageList,
        parameterLists = f.model.parameterLists,
        minimalPrice = None
      )
    responseWithIgnoredFields should equal(f.model)

    createResp.imageList.get.name should equal(f.imageList1.name)
    createResp.parameterLists.toList.map(_.uid) should equal(List(f.parameterList1, f.parameterList2).map(_.uid))
  }

  testR("update model") { case (client, xa) =>
    val f = modelFixture(client, xa)
    val createResp =
      client.createModel(CreateModelRequest(f.modelParentName, Some(f.model)), new Metadata()).unsafeRunSync()
    val newModel = createResp.copy(displayName = "new-display-name", description = "new-description")
    val update = UpdateModelRequest(Some(newModel), Some(FieldMask.of(Seq("display_name", "description"))))
    val updateResponse = client.updateModel(update, new Metadata()).unsafeRunSync()
    updateResponse should equal(newModel)

  // TODO update category
  // TODO forbidden parameterListUpdates
  }

  testR("deleteModel") { case (client, xa) =>
    val f = modelFixture(client, xa)
    val ex = intercept[StatusRuntimeException] {
      client
        .deleteModel(DeleteModelRequest(s"${f.modelParentName}/${UUID.randomUUID().toString}"), new Metadata())
        .unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.Code.NOT_FOUND)
    val createResp =
      client.createModel(CreateModelRequest(f.modelParentName, Some(f.model)), new Metadata()).unsafeRunSync()
    client.deleteModel(DeleteModelRequest(createResp.name), new Metadata()).unsafeRunSync()
    val ex2 = intercept[StatusRuntimeException] {
      client.getModel(GetModelRequest(createResp.name), new Metadata()).unsafeRunSync()
    }
    ex2.getStatus.getCode should equal(Status.Code.NOT_FOUND)
  }

  testR("listModels") { case (client, xa) =>
    val f = modelFixture(client, xa)
    val model1 =
      client
        .createModel(CreateModelRequest(f.modelParentName, Some(f.model.copy(readableId = "1"))), new Metadata())
        .unsafeRunSync()
    val model2 =
      client
        .createModel(CreateModelRequest(f.modelParentName, Some(f.model.copy(readableId = "2"))), new Metadata())
        .unsafeRunSync()
    val model3 =
      client
        .createModel(CreateModelRequest(f.modelParentName, Some(f.model.copy(readableId = "3"))), new Metadata())
        .unsafeRunSync()

    val resp = client.listModels(ListModelsRequest(f.modelParentName, 2), new Metadata()).unsafeRunSync()
    resp.models should equal(List(model3, model2))

    val resp2 =
      client.listModels(ListModelsRequest(f.modelParentName, 2, resp.nextPageToken), new Metadata()).unsafeRunSync()
    resp2.models should equal(List(model1))
  }

  // products

  testR("create product") { case (client, xa) =>
    val f = productFixture(client, xa)
    val res = client
      .createProduct(CreateProductRequest(f.productParent, Some(f.product)), new Metadata())
      .unsafeRunSync()

    res.copy(name = "", uid = "", displayName = "") should equal(f.product)
    res.displayName should not be empty
  }

  testR("get product") { case (client, xa) =>
    val f = productFixture(client, xa)

    val ex = intercept[StatusRuntimeException] {
      client
        .getProduct(GetProductRequest(s"${f.productParent}/${UUID.randomUUID().toString}"), new Metadata())
        .unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.NOT_FOUND.getCode)

    val res = client
      .createProduct(CreateProductRequest(f.productParent, Some(f.product)), new Metadata())
      .unsafeRunSync()

    val getResp = client.getProduct(GetProductRequest(res.name), new Metadata()).unsafeRunSync()
    getResp should equal(res)
  }

  testR("list products") { case (client, xa) =>
    val f = productFixture(client, xa)
    val listResp = client.listProducts(ListProductsRequest(f.productParent, 1, ""), new Metadata()).unsafeRunSync()
    listResp.products.size should equal(0)

    val product1 =
      client
        .createProduct(CreateProductRequest(f.productParent, f.product.some), new Metadata())
        .unsafeRunSync()
    val product2 =
      client
        .createProduct(CreateProductRequest(f.productParent, f.product2.some), new Metadata())
        .unsafeRunSync()
    val product3 =
      client
        .createProduct(CreateProductRequest(f.productParent, f.product3.some), new Metadata())
        .unsafeRunSync()

    val resp1 = client.listProducts(ListProductsRequest(f.productParent, 2), new Metadata()).unsafeRunSync()
    resp1.products.size should equal(2)

    val resp2 =
      client
        .listProducts(ListProductsRequest(f.productParent, 2, resp1.nextPageToken), new Metadata())
        .unsafeRunSync()
    resp2.products.size should equal(1)

    (resp1.products ++ resp2.products).toSet should equal(Set(product1, product2, product3))

    // default should not be 0
    val resp3 =
      client
        .listProducts(ListProductsRequest(f.productParent, 0, ""), new Metadata())
        .unsafeRunSync()
    resp3.products.size should not equal (0)
  }

  testR("delete product") { case (client, xa) =>
    val f = productFixture(client, xa)
    val ex = intercept[StatusRuntimeException] {
      client
        .deleteProduct(DeleteProductRequest(s"${f.productParent}/${UUID.randomUUID().toString}"), new Metadata())
        .unsafeRunSync()
    }
    ex.getStatus.getCode should equal(Status.NOT_FOUND.getCode)

    val resp =
      client.createProduct(CreateProductRequest(f.productParent, f.product.some), new Metadata()).unsafeRunSync()
    client.deleteProduct(DeleteProductRequest(resp.name), new Metadata()).unsafeRunSync()
    val ex2 = intercept[StatusRuntimeException] {
      client.getProduct(GetProductRequest(resp.name), new Metadata()).unsafeRunSync()
    }
    ex2.getStatus.getCode should equal(ex.getStatus.getCode)
  }

  private def productFixture(client: CatalogFs2Grpc[IO, Metadata], xa: Transactor[IO]) =
    new {
      val category1 =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync()
      val imageList1 = createImageList(client)
      val image = api.Image("src", "alt")
      private val param1_1: api.Parameter = api.Parameter(UUID.randomUUID().toString, "param-name-1", Some(image))
      private val param1_2: api.Parameter = api.Parameter(UUID.randomUUID().toString, "param-name-2", Some(image))
      val parameterList1 = api.ParameterList(
        UUID.randomUUID().toString,
        "",
        List(param1_1, param1_2)
      )
      private val param2_1: api.Parameter = api.Parameter(UUID.randomUUID().toString, "param-name-3", Some(image))
      private val param2_2: api.Parameter = api.Parameter(UUID.randomUUID().toString, "param-name-4", Some(image))
      val parameterList2 = api.ParameterList(UUID.randomUUID().toString, "", List(param2_1, param2_2))
      createParameterList(parameterList1).transact(xa).unsafeRunSync()
      createParameterList(parameterList2).transact(xa).unsafeRunSync()
      val modelParentName = s"${category1.name}/models"
      val model = Model(
        "",
        "",
        "some-model",
        "some-display-name",
        "descr",
        Some(imageList1),
        parameterLists = List(parameterList1, parameterList2)
      )
      val createdModel =
        client.createModel(CreateModelRequest(modelParentName, Some(model)), new Metadata()).unsafeRunSync()
      val productParent = s"${createdModel.name}/products"
      val product = Product(
        "",
        "",
        "",
        createdModel.uid,
        Some(imageList1),
        Some(Product.Price(Some(Money(10)))),
        Seq(param1_1.uid, param2_1.uid)
      )

      val product2 = Product(
        "",
        "",
        "",
        createdModel.uid,
        Some(imageList1),
        Some(Product.Price(Some(Money(10)))),
        Seq(param1_2.uid, param2_2.uid)
      )

      val product3 = Product(
        "",
        "",
        "",
        createdModel.uid,
        Some(imageList1),
        Some(Product.Price(Some(Money(10)))),
        Seq(param1_1.uid, param2_2.uid)
      )
    }

  private def createParameterList(parameterList: api.ParameterList): ConnectionIO[Unit] = {
    def createParameter(listId: String, p: api.Parameter, order: Int): ConnectionIO[Unit] =
      for {
        imageId <-
          sql"""insert into images (src, alt) VALUES (${p.image.get.src}, ${p.image.get.alt})""".update
            .withUniqueGeneratedKeys[UUID]("id")
        _ <- sql"""
       INSERT INTO parameters (id, display_name, image_id, list_order, parameter_list_id)
       VALUES (${p.uid}::uuid, ${p.displayName}, $imageId, $order, $listId::uuid)
       """.update.run
      } yield ()

    for {
      _ <- sql"""INSERT INTO parameter_lists (id, display_name)
          VALUES (${parameterList.uid}::uuid, ${parameterList.displayName})""".update.run
      _ <- parameterList.parameters.zipWithIndex.traverse { case (p, i) => createParameter(parameterList.uid, p, i) }
    } yield ()
  }

}
