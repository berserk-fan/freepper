package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie._
import ua.pomo.catalog.domain.product._
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

class ProductRepositoryImpl extends AbstractPostgresRepository[ProductCrud](ProductQueries) {
  override protected def idSelector: ProductId => ProductSelector = ProductSelector.IdIs.apply
}

object ProductRepositoryImpl {
  def apply(): ProductRepository[ConnectionIO] = {
    new ProductRepositoryImpl()
  }
}
