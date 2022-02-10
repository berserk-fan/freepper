package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image.ImageListRepository
import ua.pomo.catalog.domain.param.{ParamListDisplayName, ParameterList, ParameterListId}
import ua.pomo.catalog.domain.product.{ProductId, ProductQuery, ProductSelector}
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class ProductRepositoryImplTest extends DbUnitTestSuite {
  private val imageListRepo: ImageListRepository[ConnectionIO] = ImageListRepositoryImpl()
  private val categoryRepo = CategoryRepositoryImpl()
  private val modelPostgres = ModelRepositoryImpl(imageListRepo)
  private val modelInMemory = ModelRepositoryImpl.makeInMemory[ConnectionIO].trRun()
  private val productPostgres = ProductRepositoryImpl()
  private val productInMemory = InMemoryProductRepositoryImpl[ConnectionIO].trRun()

  test("queries") {
    import ProductRepositoryImpl.Queries

    val id = ProductId(UUID.randomUUID())
    check(Queries.find(ProductQuery(PageToken.NonEmpty(10, 5), ProductSelector.IdIs(id))))
  }

  Seq(productPostgres, productInMemory).foreach { impl =>
    test("create get delete get " ++ impl.getClass.getSimpleName) {
      val catId = categoryRepo.create(Generators.Category.create.sample.get).trRun()
      val imageListId = imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
      val modelId = modelPostgres
        .create(Generators.Model.createGen(imageListId, List.empty).sample.get.copy(categoryId = catId))
        .trRun()
      forAll(Generators.Product.createCommand(imageListId, modelId)) { create =>
        val id = impl.create(create).trRun()
        impl.find(id).trRun() shouldBe defined
        impl.delete(id).trRun()
        impl.find(id).trRun() should equal(None)
      }
    }
  }
//
//  test("insert wrong parameter list should throw exception") {
//    val catId = categoryRepo.create(Generators.Category.create.sample.get).trRun()
//    val imageListId = imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
//    val parameterLists = List(ParameterList(ParameterListId(UUID.randomUUID()), ParamListDisplayName("")))
//
//    val modelId = modelPostgres
//      .create(Generators.Model.createGen(imageListId, parameterLists).sample.get.copy(categoryId = catId))
//      .trRun()
//  }
}
