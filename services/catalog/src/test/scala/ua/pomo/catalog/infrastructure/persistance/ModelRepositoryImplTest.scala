package ua.pomo.catalog.infrastructure.persistance

import doobie.ConnectionIO
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class ModelRepositoryImplTest extends DbUnitTestSuite {
  import ModelRepositoryImpl.Queries

  private val imageListRepo: ImageListRepository[ConnectionIO] = ImageListRepositoryImpl()
  private val categoryRepo = CategoryRepositoryImpl()
  private val modelPostgres = ModelRepositoryImpl(imageListRepo)
  private val modelInMemory = ModelRepositoryImpl.makeInMemory[ConnectionIO].trRun()

  test("queries") {
    val modelId = ModelId(UUID.randomUUID())
    val categoryId = CategoryUUID(UUID.randomUUID())
    val imageListId = ImageListId(UUID.randomUUID())
    check(Queries.getModel(modelId))
    check(Queries.find(categoryId, 10, 0))
    check(Queries.delete(modelId))
    check(Queries.create(Generators.Model.createGen(imageListId).sample.get))
    check(Queries.update(Generators.Model.updateGen(imageListId, categoryId).sample.get))
  }

  Seq(modelInMemory, modelPostgres).foreach { modelRepo =>
    test(s"create get delete flow ${modelRepo.getClass.getSimpleName}") {
      val category = Generators.Category.create.sample.get
      val categoryId = categoryRepo.create(category).trRun()

      val imageListId1 = imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
      val imageList1 = imageListRepo.get(imageListId1).trRun()

      forAll(Generators.Model.createGen(imageListId1)) { createModel1 =>
        val createModel = createModel1.copy(categoryId = categoryId)

        val id = modelRepo.create(createModel).trRun()
        val found = modelRepo.get(id).trRun()

        createModel.categoryId should equal(found.categoryId)
        createModel.description should equal(found.description)
        createModel.displayName should equal(found.displayName)
        createModel.readableId should equal(found.readableId)
        createModel.imageListId should equal(found.imageList.id)

        modelRepo.delete(id).trRun()
      }
    }

    test(s"update ${modelRepo.getClass.getSimpleName}") {
      val category1 = Generators.Category.create.sample.get
      val categoryId1 = categoryRepo.create(category1).trRun()
      val category2 = Generators.Category.create.sample.get
      val categoryId2 = categoryRepo.create(category2).trRun()

      val imageListId1 = imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()
      val imageList1 = imageListRepo.get(imageListId1).trRun()
      val imageListId2 = imageListRepo.create(Generators.ImageList.gen.sample.get).trRun()

      val createModel =
        Generators.Model.createGen(imageListId1).sample.get.copy(categoryId = categoryId1, imageListId = imageList1.id)
      val modelId = modelRepo.create(createModel).trRun()

      forAll(Generators.Model.updateGen(imageListId2, categoryId2)) { update =>
        modelRepo.update(update.copy(id = modelId)).trRun()
        val model = modelRepo.get(modelId).trRun()

        update.categoryId.foreach(_ should equal(model.categoryId))
        update.description.foreach(_ should equal(model.description))
        update.displayName.foreach(_ should equal(model.displayName))
        update.readableId.foreach(_ should equal(model.readableId))
        update.imageListId.foreach(_ should equal(model.imageList.id))
      }
    }
  }
}
