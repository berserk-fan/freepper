package ua.pomo.catalog.infrastructure.persistance.postgres

import shapeless._
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class ModelRepositoryImplTest extends DbUnitTestSuite {

  import ModelRepositoryImpl.Queries

  private val imageListRepo: ImageListRepository[doobie.ConnectionIO] = ImageListRepositoryImpl()
  private val modelRepo = ModelRepositoryImpl(imageListRepo)
  private val categoryRepo = CategoryRepositoryImpl()

  test("queries") {
    val modelId = ModelId(ModelUUID(UUID.randomUUID()))
    val categoryId = CategoryUUID(UUID.randomUUID())
    val imageListId = ImageListId(UUID.randomUUID())
    check(Queries.getModel(modelId))
    check(Queries.find(FindModel(categoryId, 10, 0)))
    check(Queries.delete(modelId))
    check(Queries.create(Generators.Model.self.sample.get, ImageListId(UUID.randomUUID())))
    check(Queries.update(Generators.Model.update(imageListId, categoryId).sample.get))
  }


  test("create get delete flow") {
    val category = Generators.Category.self.sample.get
    val categoryId = categoryRepo.create(category).trRun()

    forAll(Generators.Model.self) { model1 =>
      val model = model1.copy(categoryId = categoryId)

      val id = modelRepo.create(model).trRun()
      val found = modelRepo.get(ModelId(id)).trRun()

      model.categoryId should equal(found.categoryId)
      model.description should equal(found.description)
      model.displayName should equal(found.displayName)
      model.readableId should equal(found.readableId)
      model.imageList.displayName should equal(found.imageList.displayName)
      def ignorer(images: List[Image]): Set[(ImageSrc, ImageAlt)] = images.map(im => (im.src, im.alt)).toSet
      ignorer(model.imageList.images) should equal(ignorer(found.imageList.images))

      modelRepo.delete(ModelId(id)).trRun()
    }
  }

  test("update") {
    val category1 = Generators.Category.self.sample.get
    val categoryId1 = categoryRepo.create(category1).trRun()
    val category2 = Generators.Category.self.sample.get
    val categoryId2 = categoryRepo.create(category2).trRun()

    val imageListId1 = imageListRepo.create(Generators.ImageList.self.sample.get).trRun()
    val imageList1 = imageListRepo.get(imageListId1).trRun()
    val imageListId2 = imageListRepo.create(Generators.ImageList.self.sample.get).trRun()

    val model = Generators.Model.self.sample.get.copy(categoryId = categoryId1, imageList = imageList1)
    val modelId = modelRepo.create(model).trRun()

    forAll(Generators.Model.update(imageListId2, categoryId2)) { update =>
      modelRepo.update(update).trRun()
      val model = modelRepo.get(ModelId(modelId)).trRun()

      update.categoryId.foreach(_ should equal (model.categoryId))
      update.description.foreach(_ should equal (model.description))
      update.displayName.foreach(_ should equal (model.displayName))
      update.readableId.foreach(_ should equal (model.readableId))
      update.imageListId.foreach(_ should equal (model.imageList.id))
    }
  }
}
