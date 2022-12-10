package ua.pomo.catalog.infrastructure.persistance.postgres

import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.ImageCrud

class CategoryPostgresTest extends CatalogRepositoryAbstractTest[CategoryCrud](DbModuleTest.categoryPostgres)
class CategoryInMemoryTest extends CatalogRepositoryAbstractTest[CategoryCrud](DbModuleTest.categoryInMemory)
class ImagePostgresTest extends CatalogRepositoryAbstractTest[ImageCrud](DbModuleTest.imagePostgres)

