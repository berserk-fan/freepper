package ua.pomo.catalog.infrastructure.persistance.postgres

import org.scalacheck.Gen
import ua.pomo.catalog.domain.CategoryCrud
import ua.pomo.catalog.domain.category.{Category, CategoryUUID, CreateCategory, UpdateCategory}
import ua.pomo.catalog.shared.FixturesV2.CategoryFixture
import ua.pomo.catalog.shared.Generators
import ua.pomo.common.infrastructure.persistance.postgres.Generators

object DbGenerators {
  object CategoryGenerators extends Generators[CategoryCrud.type] {
    override def create(f: CategoryFixture): Gen[CreateCategory] = Generators.Category.create

    override def update(f: CategoryFixture): Gen[UpdateCategory] =
      Generators.Category.update.map(_.copy(id = f.categoryId1))

    override def genE(f: CategoryFixture): Gen[Category] = Generators.Category.gen

    override def id: Gen[CategoryUUID] = Generators.Category.catId
  }
}
