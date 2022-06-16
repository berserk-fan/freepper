package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits._
import org.scalacheck.Gen
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.{CategoryRepository, CategoryUUID}
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Fixtures, Generators, HasDbResources, Resources}

import java.util.UUID

class ModelRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution with Fixtures {
  import ModelRepositoryImpl.Queries
  case class TestResources(
      categoryRepo: CategoryRepository[ConnectionIO],
      imageListRepo: ImageListRepository[ConnectionIO],
      postgres: ModelRepository[ConnectionIO],
      db: DbResources,
      impls: Seq[Impl]
  ) extends HasDbResources
      with HasImpls

  override type Res = TestResources
  override val resourcePerTest: Boolean = true
  override type Impl = ModelRepository[ConnectionIO]
  override def names: Seq[String] = Seq("postgres", "inmemory")
  override protected def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      imageListRepo <- Resource.pure(ImageListRepositoryImpl())
      categoryRepo = CategoryRepositoryImpl()
      modelRepo = ModelRepositoryImpl()
      modelInMemory <- Resource.eval(ModelRepositoryImpl.makeInMemory[ConnectionIO]).mapK(db.xa.trans)
    } yield TestResources(categoryRepo, imageListRepo, modelRepo, db, Seq(modelRepo, modelInMemory))

  test("queries") {
    val modelId = ModelId(UUID.randomUUID())
    val categoryId = CategoryUUID(UUID.randomUUID())
    val imageListId = ImageListId(UUID.randomUUID())
    check(Queries.find(ModelQuery(ModelSelector.CategoryIdIs(categoryId), PageToken.NonEmpty(10, 0))))
    check(Queries.delete(modelId))
    check(Queries.create(Generators.Model.createGen(imageListId, List.empty).sample.get)._1)
    check(Queries.update(Generators.Model.updateGen(imageListId, categoryId).sample.get))
  }

  testEachImplR(s"create get delete flow") { (res, impl) =>
    abstract class fa(
        val categoryRepo: CategoryRepository[ConnectionIO],
        val imageListRepo: ImageListRepository[ConnectionIO]
    ) extends ImageListFixture
        with CategoryFixture
    object f extends fa(res.categoryRepo, res.imageListRepo)

    forAll(Generators.Model.createGen(f.imageListId1, List.empty)) { createModel1 =>
      val createModel = createModel1.copy(categoryId = f.categoryId1)

      val id = impl.create(createModel).trRun()
      val found = impl.get(id).trRun()

      createModel.categoryId should equal(found.categoryUid)
      createModel.description should equal(found.description)
      createModel.displayName should equal(found.displayName)
      createModel.readableId should equal(found.readableId)
      createModel.imageListId should equal(found.imageList.id)

      impl.delete(id).trRun()
    }
  }

  testEachImplR(s"update") { (res, impl) =>
    abstract class fa(
        val imageListRepo: ImageListRepository[ConnectionIO],
        val categoryRepo: CategoryRepository[ConnectionIO],
        val modelRepo: ModelRepository[ConnectionIO]
    ) extends ModelFixture
    object f extends fa(res.imageListRepo, res.categoryRepo, impl)

    forAll(Generators.Model.updateGen(f.imageListId2, f.categoryId2)) { update =>
      impl.update(update.copy(id = f.modelId)).trRun()
      val model = impl.get(f.modelId).trRun()

      update.categoryId.foreach(_ should equal(model.categoryUid))
      update.description.foreach(_ should equal(model.description))
      update.displayName.foreach(_ should equal(model.displayName))
      update.readableId.foreach(_ should equal(model.readableId))
      update.imageListId.foreach(_ should equal(model.imageList.id))
    }
  }
}
