package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryRepository
import ua.pomo.catalog.domain.imageList.{ImageListId, ImageListRepository}
import ua.pomo.catalog.domain.model.{ModelId, ModelRepository}
import ua.pomo.catalog.domain.product.{ProductId, ProductQuery, ProductRepository, ProductSelector}
import ua.pomo.catalog.shared._

import java.util.UUID

class ProductRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution with Fixtures {
  case class TestResources(
      imageListRepo: ImageListRepository[ConnectionIO],
      categoryRepo: CategoryRepository[ConnectionIO],
      modelRepo: ModelRepository[ConnectionIO],
      postgres: ProductRepository[ConnectionIO],
      impls: Seq[(String, Impl)],
      db: DbResources
  )

  override type TestResource = TestResources
  override type Impl = ProductRepository[ConnectionIO]
  override val resourcePerTest: Boolean = true
  override def getDbResources(resources: TestResources): DbResources = resources.db
  override def getImpls(resources: TestResources): Seq[(String, Impl)] = resources.impls
  override def names: Seq[String] = Seq("postgres", "inmemory")
  override protected def resource: Resource[IO, TestResource] =
    for {
      db <- Resources.dbTest
      productInMemory <- Resource.eval(InMemoryProductRepositoryImpl[ConnectionIO]).mapK(db.xa.trans)
      productPostgres = ProductRepositoryImpl()
    } yield TestResources(
      ImageListRepositoryImpl(),
      CategoryRepositoryImpl(),
      ModelRepositoryImpl(),
      productPostgres,
      Seq(("postgres", productPostgres), ("inmemory", productInMemory)),
      db
    )

  test("queries") {
    import ProductRepositoryImpl.Queries

    val id = ProductId(UUID.randomUUID())
    val ilid = ImageListId(UUID.randomUUID())
    val modelId = ModelId(UUID.randomUUID())
    check(Queries.find(ProductQuery(PageToken.NonEmpty(10, 5), ProductSelector.IdIs(id))))
    forAll(Generators.Product.update) { update =>
      check(Queries.update(update))
    }
    forAll(Generators.Product.create(ilid, modelId, List())) { create =>
      check(Queries.create(create))
    }
    check(Queries.delete(id))
  }

  testEachImplR("create get delete get") { (res, impl) =>
    abstract class fa(
        val modelRepo: ModelRepository[ConnectionIO],
        val imageListRepo: ImageListRepository[ConnectionIO],
        val categoryRepo: CategoryRepository[ConnectionIO]
    ) extends ModelFixture
    object f extends fa(res.modelRepo, res.imageListRepo, res.categoryRepo)
    forAll(Generators.Product.create(f.imageListId1, f.modelId, List())) { create =>
      val id = impl.create(create).trRun()
      impl.find(id).trRun() shouldBe defined
      impl.delete(id).trRun()
      impl.find(id).trRun() should equal(None)
    }
  }

  testR("insert wrong parameter list should throw exception") { res =>
    abstract class fa(
        val modelRepo: ModelRepository[ConnectionIO],
        val categoryRepo: CategoryRepository[ConnectionIO],
        val imageListRepo: ImageListRepository[ConnectionIO]
    ) extends ModelFixture
    object f extends fa(res.modelRepo, res.categoryRepo, res.imageListRepo)

    // should throw
    intercept[Exception] {
      res.postgres.create(Generators.Product.create(f.imageListId1, f.modelWithParamList1Id, List()).sample.get).trRun()
    }

    noException should be thrownBy res.postgres
      .create(Generators.Product.create(f.imageListId1, f.modelWithParamList1Id, List(f.parameterId1)).sample.get)
      .trRun()
  }

  testR("forbid parameter_ids updates") { res =>
    abstract class fa(
        val modelRepo: ModelRepository[ConnectionIO],
        val categoryRepo: CategoryRepository[ConnectionIO],
        val imageListRepo: ImageListRepository[ConnectionIO]
    ) extends ModelFixture
    object f extends fa(res.modelRepo, res.categoryRepo, res.imageListRepo)

    val id = res.postgres
      .create(Generators.Product.create(f.imageListId1, f.modelWithParamList1Id, List(f.parameterId1)).sample.get)
      .trRun()
      .value
    val ex = intercept[Exception] {
      sql"""update products SET parameter_ids=ARRAY[]::UUID[] WHERE id=$id""".update.run.trRun()
    }
    ex.getMessage should include("forbidden to update parameter_ids")
  }
}
