package ua.pomo.catalog.domain

import ua.pomo.catalog.shared.FixturesV2.CategoryFixture
import ua.pomo.common.infrastructure.persistance.postgres.TestCrud

object CategoryCrud extends category.Crud with TestCrud {
  override type Fixture = CategoryFixture
}
