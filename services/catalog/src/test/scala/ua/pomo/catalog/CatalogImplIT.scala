package ua.pomo.catalog

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import cats.implicits.{catsSyntaxOptionId, toTraverseOps}
import com.google.protobuf.field_mask.FieldMask
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{ConnectionIO, Transactor}
import io.grpc.{Metadata, Status, StatusRuntimeException}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api._
import ua.pomo.catalog.shared.{HasIOResource, Resources}

import java.util.UUID
import scala.language.reflectiveCalls

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
    ImageList("", "", "some name", Seq(Image("some-src", "some-alt"), Image("some-src2", "some-alt2")))

  testR("create image list") {
    case (client, _) =>
      val createResp = createImageList(client)
      createResp.copy(name = "", uid = "") should equal(imageList)
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

  def modelFixture(client: CatalogFs2Grpc[IO, Metadata], xa: Transactor[IO]) =
    new {
      val category1 =
        client.createCategory(CreateCategoryRequest("categories", Some(category)), new Metadata()).unsafeRunSync()
      val imageList1 =
        client.createImageList(CreateImageListRequest("imageLists", Some(imageList)), new Metadata()).unsafeRunSync()
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
        Model.ImageList.ImageListName(imageList1.name),
        parameterLists = Model.ParameterListsOneof.ParameterListIds(
          Model.ParameterListIds(List(parameterList1.uid, parameterList2.uid)))
      )
    }

  testR("create model") {
    case (client, xa) =>
      val f = modelFixture(client, xa)

      val createResp =
        client
          .createModel(CreateModelRequest(f.modelParentName, Some(f.model)), new Metadata())
          .unsafeRunSync()
      val responseWithIgnoredFields =
        createResp.copy(name = "",
                        uid = "",
                        imageList = f.model.imageList,
                        parameterLists = f.model.parameterLists,
                        minimalPrice = None)
      responseWithIgnoredFields should equal(f.model)

      createResp.imageList.imageListData should equal(Some(f.imageList1))
      createResp.parameterLists.parameterListsData.map(_.value.toList) should equal(
        Some(List(f.parameterList1, f.parameterList2)))
  }

  testR("update model") {
    case (client, xa) =>
      val f = modelFixture(client, xa)
      val createResp =
        client.createModel(CreateModelRequest(f.modelParentName, Some(f.model)), new Metadata()).unsafeRunSync()
      val newModel = createResp.copy(displayName = "new-display-name", description = "new-description")
      val update = UpdateModelRequest(Some(newModel), Some(FieldMask.of(Seq("display_name", "description"))))
      val updateResponse = client.updateModel(update, new Metadata()).unsafeRunSync()
      updateResponse should equal(newModel)

    //TODO update category
    //TODO forbidden parameterListUpdates
  }

  testR("deleteModel") {
    case (client, xa) =>
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

  testR("listModels") {
    case (client, xa) =>
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

  //products

  testR("create product") {
    case (client, xa) =>
      val f = productFixture(client, xa)
      val res = client
        .createProduct(CreateProductRequest(f.productParent, Some(f.product)), new Metadata())
        .unsafeRunSync()

      res.copy(name = "", uid = "") should equal(f.product)
  }

  testR("get product") {
    case (client, xa) =>
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

  testR("list products") {
    case (client, xa) =>
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
  }

  testR("delete product") {
    case (client, xa) =>
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
      val imageList1 =
        client.createImageList(CreateImageListRequest("imageLists", Some(imageList)), new Metadata()).unsafeRunSync()
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
        Model.ImageList.ImageListName(imageList1.name),
        parameterLists = Model.ParameterListsOneof.ParameterListIds(
          Model.ParameterListIds(List(parameterList1.uid, parameterList2.uid)))
      )
      val createdModel =
        client.createModel(CreateModelRequest(modelParentName, Some(model)), new Metadata()).unsafeRunSync()
      val productParent = s"${createdModel.name}/products"
      val product = Product(
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
        createdModel.uid,
        Some(imageList1),
        Some(Product.Price(Some(Money(10)))),
        Seq(param1_2.uid, param2_2.uid)
      )

      val product3 = Product(
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
        imageId <- sql"""insert into images (src, alt, list_order) VALUES (${p.image.get.src}, ${p.image.get.alt}, 1)""".update
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
