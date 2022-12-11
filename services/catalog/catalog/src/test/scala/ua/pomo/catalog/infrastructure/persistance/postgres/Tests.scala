package ua.pomo.catalog.infrastructure.persistance.postgres

import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.ImageCrud
import ua.pomo.common.infrastructure.persistance.postgres.RepositoryAbstractTest

class CategoryPostgresTest extends CatalogRepositoryAbstractTest[CategoryCrud](DbModuleTest.categoryPostgres)
class CategoryInMemoryTest extends CatalogRepositoryAbstractTest[CategoryCrud](DbModuleTest.categoryInMemory)
class ImagePostgresTest extends CatalogRepositoryAbstractTest[ImageCrud](DbModuleTest.imagePostgres) {
  override def ignoredContracts: Set[RepositoryAbstractTest.TestContract] = Set(RepositoryAbstractTest.TestContract.UpdateContract)
}

