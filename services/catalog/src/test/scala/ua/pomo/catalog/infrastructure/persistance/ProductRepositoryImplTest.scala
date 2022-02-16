package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import ua.pomo.catalog.domain.PageToken
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
                           modelPostgres: ModelRepository[ConnectionIO],
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
    } yield TestResources(imageListRepo, categoryRepo, modelRepo, Seq(productPostgres, productInMemory), db)

  test("queries") {
    import ProductRepositoryImpl.Queries

    val id = ProductId(UUID.randomUUID())
    val ilid = ImageListId(UUID.randomUUID())
    val modelId = ModelId(UUID.randomUUID())
    check(Queries.find(ProductQuery(PageToken.NonEmpty(10, 5), ProductSelector.IdIs(id))))
    forAll(Generators.Product.update) { update =>
      check(Queries.update(update))
    }
    forAll(Generators.Product.сreate(ilid, modelId)) { create =>
      check(Queries.create(create))
    }
    check(Queries.delete(id))
  }

  testEachImplR("create get delete get") { (res, impl) =>
    val catId = res.categoryRepo.create(Generators.Category.create.sample.get).trRun()
    val imageListId = res.imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()

    val modelId = res.modelPostgres
      .create(Generators.Model.createGen(imageListId, List.empty).sample.get.copy(categoryId = catId))
      .trRun()
    forAll(Generators.Product.сreate(imageListId, modelId)) { create =>
      val id = impl.create(create).trRun()
      impl.find(id).trRun() shouldBe defined
      impl.delete(id).trRun()
      impl.find(id).trRun() should equal(None)
    }
  }
//  testR("insert wrong parameter list should throw exception") { res =>
//    val catId = res.categoryRepo.create(Generators.Category.create.sample.get).trRun()
//    val imageListId = res.imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
//    val parameterLists = List(ParameterList(ParameterListId(UUID.randomUUID()), ParamListDisplayName("")))
//
//    val modelId = modelPostgres
//      .create(Generators.Model.createGen(imageListId, parameterLists).sample.get.copy(categoryId = catId))
//      .trRun()
//  }
}
