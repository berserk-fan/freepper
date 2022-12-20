package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.IO
import doobie.ConnectionIO
import org.typelevel.log4cats.slf4j.loggerFactoryforSync
import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.ImageCrud
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.infrastructure.persistance.postgres.AbstractRepositoryTest

class CategoryPostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, CategoryCrud](CatalogEntityTests.postgres[CategoryCrud])
class CategoryInMemoryRepositoryTest
    extends CatalogAbstractRepositoryTest[IO, CategoryCrud](CatalogEntityTests.inmemory[CategoryCrud])

class ImagePostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, ImageCrud](CatalogEntityTests.postgres[ImageCrud]) {
  override def ignoredContracts: Set[AbstractRepositoryTest.TestContract] = Set(
    AbstractRepositoryTest.TestContract.UpdateContract
  )
}
class ImageListPostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, ImageListCrud](CatalogEntityTests.postgres[ImageListCrud])

class ImageListInMemoryRepositoryTest
    extends CatalogAbstractRepositoryTest[IO, ImageListCrud](CatalogEntityTests.inmemory[ImageListCrud])

class ParameterListPostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, ParameterListCrud](
      CatalogEntityTests.postgres[ParameterListCrud]
    )

class ModelPostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, ModelCrud](CatalogEntityTests.postgres[ModelCrud])
class ModelInMemoryRepositoryTest
    extends CatalogAbstractRepositoryTest[IO, ModelCrud](CatalogEntityTests.inmemory[ModelCrud])

class ProductPostgresRepositoryTest
    extends CatalogAbstractRepositoryTest[ConnectionIO, ProductCrud](CatalogEntityTests.postgres[ProductCrud]) {
//  testA("insert wrong parameter list should throw exception") { res =>
//    val create = res.generators.create(List("modelWithParameters")).sample.get.copy(parameterIds = List())
//    res.repository
//      .create(create)
//      .redeemWith[Assertion](
//        _ => Sync[ConnectionIO].pure(succeed),
//        _ => Sync[ConnectionIO].pure(fail("insert wrong parameter list has not thrown an exception"))
//      )
//  }

//  testA("forbid parameter_ids updates") { res =>
//    val c = res.generators.create(List("modelWithParameters")).sample.get
//
//    res.repository
//      .create(c)
//      .flatMap { id =>
//        sql"""update products SET parameter_ids=ARRAY[]::UUID[] WHERE id=$id""".update.run
//      }
//      .redeemWith[Assertion](
//        ex => ex.getMessage should include("forbidden to update parameter_ids"),
//        _ => Sync[ConnectionIO].pure(fail("update of parameter_ids has not thrown an exception"))
//      )
//  }
}

class ProductInMemoryRepositoryTest
    extends CatalogAbstractRepositoryTest[IO, ProductCrud](CatalogEntityTests.inmemory[ProductCrud])
