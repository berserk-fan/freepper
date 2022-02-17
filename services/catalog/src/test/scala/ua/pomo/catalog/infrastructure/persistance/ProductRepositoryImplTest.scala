package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits._
import ua.pomo.catalog.domain.{PageToken, image}
import ua.pomo.catalog.domain.category.CategoryRepository
import ua.pomo.catalog.domain.image.{ImageListId, ImageListRepository}
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.model.{ModelId, ModelRepository}
import ua.pomo.catalog.domain.product.{ProductId, ProductQuery, ProductRepository, ProductSelector}
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}

import java.util.UUID

class ProductRepositoryImplTest extends DbUnitTestSuite {
  case class TestResources(imageListRepo: ImageListRepository[ConnectionIO],
                           categoryRepo: CategoryRepository[ConnectionIO],
                           modelRepo: ModelRepository[ConnectionIO],
                           postgres: ProductRepository[ConnectionIO],
                           impls: Seq[Impl],
                           db: DbResources)
      extends HasDbResources
      with HasImpls

  override type Res = TestResources
  override type Impl = ProductRepository[ConnectionIO]
  override def names: Seq[String] = Seq("postgres", "inmemory")
  override protected def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      imageListRepo = ImageListRepositoryImpl()
      categoryRepo = CategoryRepositoryImpl()
      modelRepo = ModelRepositoryImpl(imageListRepo)
      productPostgres = ProductRepositoryImpl()
      productInMemory <- Resource.eval(InMemoryProductRepositoryImpl[ConnectionIO]).mapK(db.xa.trans)
    } yield
      TestResources(imageListRepo, categoryRepo, modelRepo, productPostgres, Seq(productPostgres, productInMemory), db)

  test("queries") {
    import ProductRepositoryImpl.Queries

    val id = ProductId(UUID.randomUUID())
    val ilid = ImageListId(UUID.randomUUID())
    val modelId = ModelId(UUID.randomUUID())
    check(Queries.find(ProductQuery(PageToken.NonEmpty(10, 5), ProductSelector.IdIs(id))))
    forAll(Generators.Product.update) { update =>
      check(Queries.update(update))
    }
    forAll(Generators.Product.create(ilid, modelId, List())) { create =>
      check(Queries.create(create))
    }
    check(Queries.delete(id))
  }

  testEachImplR("create get delete get") { (res, impl) =>
    val imageList: image.ImageList = Generators.ImageList.gen.sample.get
    val catId = res.categoryRepo.create(Generators.Category.create.sample.get).trRun()
    val imageListId = res.imageListRepo.create(imageList).trRun()

    val modelId = res.modelRepo
      .create(Generators.Model.createGen(imageListId, List.empty).sample.get.copy(categoryId = catId))
      .trRun()
    forAll(Generators.Product.create(imageListId, modelId, List())) { create =>
      val id = impl.create(create).trRun()
      impl.find(id).trRun() shouldBe defined
      impl.delete(id).trRun()
      impl.find(id).trRun() should equal(None)
    }
  }

  testR("insert wrong parameter list should throw exception") { res =>
    val imageList: image.ImageList = Generators.ImageList.gen.filter(_.images.nonEmpty).sample.get
    val catId = res.categoryRepo.create(Generators.Category.create.sample.get).trRun()
    val imageListId = res.imageListRepo.create(imageList).trRun()
    val imageId = res.imageListRepo.get(imageListId).trRun().images.head.id

    val parameterListId = sql"""insert into parameter_lists (display_name) values ('')""".update
      .withUniqueGeneratedKeys[ParameterListId]("id")
      .trRun()
    val parameterListIds = List(parameterListId)
    val parameterId =
      sql"""insert into parameters (display_name, image_id, list_order, parameter_list_id) VALUES ('',$imageId, 1, $parameterListId)""".update
        .withUniqueGeneratedKeys[ParameterId]("id")
        .trRun()
    val modelId = res.modelRepo
      .create(Generators.Model.createGen(imageListId, parameterListIds).sample.get.copy(categoryId = catId))
      .trRun()

    intercept[Exception] {
      res.postgres.create(Generators.Product.create(imageListId, modelId, List()).sample.get).trRun()
    }

    noException should be thrownBy res.postgres
      .create(Generators.Product.create(imageListId, modelId, List(parameterId)).sample.get)
      .trRun()
  }

  testR("forbid parameter_ids updates") { res =>
    val imageList: image.ImageList = Generators.ImageList.gen.filter(_.images.nonEmpty).sample.get
    val catId = res.categoryRepo.create(Generators.Category.create.sample.get).trRun()
    val imageListId = res.imageListRepo.create(imageList).trRun()
    val imageId = res.imageListRepo.get(imageListId).trRun().images.head.id
    val parameterListId = sql"""insert into parameter_lists (display_name) values ('')""".update
      .withUniqueGeneratedKeys[ParameterListId]("id")
      .trRun()
    val parameterListIds = List(parameterListId)
    val parameterId =
      sql"""insert into parameters (display_name, image_id, list_order, parameter_list_id) VALUES ('',$imageId, 1, $parameterListId)""".update
        .withUniqueGeneratedKeys[ParameterId]("id")
        .trRun()
    val modelId = res.modelRepo
      .create(Generators.Model.createGen(imageListId, parameterListIds).sample.get.copy(categoryId = catId))
      .trRun()
    val id = res.postgres.create(Generators.Product.create(imageListId, modelId, List(parameterId)).sample.get).trRun()
    val ex = intercept[Exception] {
      sql"""update products SET parameter_ids=ARRAY[]::UUID[] WHERE id=$id""".update.run.trRun()
    }
    ex.getMessage should include("forbidden to update parameter_ids")
  }
}
