package ua.pomo.catalog.infrastructure.persistance

import ua.pomo.catalog.domain.product.ProductUUID
import ua.pomo.catalog.shared.DbUnitTestSuite

import java.util.UUID

class ProductRepositoryImplTest extends DbUnitTestSuite {
  test("queries") {
    import ProductRepositoryImpl.Queries

    check(Queries.get(ProductUUID(UUID.randomUUID())))
  }
}
