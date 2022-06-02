package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits._
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.{CategoryRepository, CategoryUUID}
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}

import java.util.UUID

class ModelRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution {
  import ModelRepositoryImpl.Queries
  case class TestResources(categoryRepo: CategoryRepository[ConnectionIO],
                           imageListRepo: ImageListRepository[ConnectionIO],
                           postgres: ModelRepository[ConnectionIO],
                           db: DbResources,
                           impls: Seq[Impl])
      extends HasDbResources
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
    val category = Generators.Category.create.sample.get
    val categoryId = res.categoryRepo.create(category).trRun()

    val imageListId1 = res.imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
    res.imageListRepo.get(imageListId1).trRun()

    forAll(Generators.Model.createGen(imageListId1, List.empty)) { createModel1 =>
      val createModel = createModel1.copy(categoryId = categoryId)

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
    val category1 = Generators.Category.create.sample.get
    val categoryId1 = res.categoryRepo.create(category1).trRun()
    val category2 = Generators.Category.create.sample.get
    val categoryId2 = res.categoryRepo.create(category2).trRun()

    val imageListId1 = res.imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
    val imageList1 = res.imageListRepo.get(imageListId1).trRun()
    val imageListId2 = res.imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()

    val createModel =
      Generators.Model
        .createGen(imageListId1, List.empty)
        .sample
        .get
        .copy(categoryId = categoryId1, imageListId = imageList1.id)
    val modelId = impl.create(createModel).trRun()

    forAll(Generators.Model.updateGen(imageListId2, categoryId2)) { update =>
      impl.update(update.copy(id = modelId)).trRun()
      val model = impl.get(modelId).trRun()

      update.categoryId.foreach(_ should equal(model.categoryUid))
      update.description.foreach(_ should equal(model.description))
      update.displayName.foreach(_ should equal(model.displayName))
      update.readableId.foreach(_ should equal(model.readableId))
      update.imageListId.foreach(_ should equal(model.imageList.id))
    }
  }
}