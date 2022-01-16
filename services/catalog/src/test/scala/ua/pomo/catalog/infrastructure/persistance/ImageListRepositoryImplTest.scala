package ua.pomo.catalog.infrastructure.persistance

import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.infrastructure.persistance.ImageListRepositoryImpl.Queries
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class ImageListRepositoryImplTest extends DbUnitTestSuite {
  val impl = ImageListRepositoryImpl()

  test("queries") {
    val id = ImageListId(UUID.randomUUID())
    val displayName = ImageListDisplayName("qq1")
    check(Queries.selectImageList(id))
    check(Queries.selectImages(id))
    check(Queries.createImageList(displayName))
    check(Queries.upsertImage)
    check(Queries.createMembership)
    check(Queries.updateImageList(id, displayName))
    check(Queries.clearMembership(id))
  }

  test("create get contract") {
    forAll(Generators.ImageList.self) { imageList =>
      val dbId = impl.create(imageList).trRun()
      val added = impl.get(dbId).trRun()
      added.copy(images = List()) should equal (imageList.copy(id = dbId, images = List()))
      added.images.map(_.src.value).sorted should equal (imageList.images.map(_.src.value).sorted)
      impl.delete(dbId).trRun()
    }
  }

  test("create find delete find") {
    forAll(Generators.ImageList.self) { imageList =>
      val dbId = impl.create(imageList).trRun()
      impl.find(dbId).trRun() shouldBe defined
      impl.delete(dbId).trRun()
      impl.find(dbId).trRun() shouldBe None
    }
  }

  test("update") {
    def nonEmptyUpdate(u: ImageListUpdate): Boolean = u.productIterator.exists {
      case x: Option[_] => x.isDefined
      case _ =>  false
    }
    forAll(Generators.ImageList.update.filter(nonEmptyUpdate)) { update =>
      val imageList = Generators.ImageList.self.sample.get

      val dbId = impl.create(imageList).trRun()
      impl.update(update.copy(id=dbId)).trRun()
      val updated = impl.get(dbId).trRun()
      update.displayName.foreach { newDisplayName =>
        updated.displayName should equal (newDisplayName)
      }
      update.images.foreach { newImages =>
        updated.images.map(_.src.value).toSet should equal (newImages.map(_.src.value).toSet)
      }

      impl.delete(dbId).trRun()
    }
  }
}
