package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image.ImageListRepository
import ua.pomo.catalog.domain.product.{ProductQuery, ProductSelector, ProductId}
import ua.pomo.catalog.shared.DbUnitTestSuite

import java.util.UUID

class ProductRepositoryImplTest extends DbUnitTestSuite {
  private val imageListRepo: ImageListRepository[ConnectionIO] = ImageListRepositoryImpl()
  private val categoryRepo = CategoryRepositoryImpl()
  private val modelPostgres = ModelRepositoryImpl(imageListRepo)
  private val modelInMemory = ModelRepositoryImpl.makeInMemory[ConnectionIO].trRun()
  private val productPostgres = ProductRepositoryImpl(imageListRepo)
  private val productInMemory = InMemoryProductRepositoryImpl[ConnectionIO].trRun()

  test("queries") {
    import ProductRepositoryImpl.Queries

    val id = ProductId(UUID.randomUUID())
    check(Queries.get(ProductQuery(PageToken.NonEmpty(10, 5), ProductSelector.IdIs(id))))
  }

  Seq(productPostgres, productInMemory).foreach { impl =>
    test("create get delete get") {

    }
  }
}
