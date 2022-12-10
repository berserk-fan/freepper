package ua.pomo.catalog.infrastructure.persistance.postgres

import ua.pomo.catalog.domain.category.{CategoryCrud, CategorySelector, CategoryUUID}
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

case class CategoryPostgresRepository private[persistance] ()  extends AbstractPostgresRepository[CategoryCrud](CategoryQueries) {
  override protected def idSelector: CategoryUUID => CategorySelector = CategorySelector.UidIs.apply
}

