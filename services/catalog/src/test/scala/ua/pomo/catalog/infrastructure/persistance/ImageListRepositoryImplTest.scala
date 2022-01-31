package ua.pomo.catalog.infrastructure.persistance

import cats.data.NonEmptyList
import doobie.ConnectionIO
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image.ImageListSelector.IdsIn
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.infrastructure.persistance.ImageListRepositoryImpl.Queries
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID

class ImageListRepositoryImplTest extends DbUnitTestSuite {
  private val postgres: ImageListRepository[ConnectionIO] = ImageListRepositoryImpl()
  private val inMemory: ImageListRepository[ConnectionIO] = InMemoryImageListRepositoryImpl()

  test("queries") {
    val id = ImageListId(UUID.randomUUID())
    val id2 = ImageListId(UUID.randomUUID())
    val displayName = ImageListDisplayName("qq1")
    check(Queries.findImageList(ImageListQuery(PageToken.NonEmpty(10, 5), IdsIn(NonEmptyList.of(id, id2)))))
    check(Queries.createImageList(displayName))
    check(Queries.upsertImage)
    check(Queries.createMembership)
    check(Queries.updateImageList(id, displayName))
    check(Queries.clearMembership(id))
  }

  Seq(postgres, inMemory).foreach { impl =>
    test(s"create get contract ${impl.getClass.getSimpleName}") {
      forAll(Generators.ImageList.gen) { imageList =>
        val dbId = impl.create(imageList).trRun()
        val added = impl.get(dbId).trRun()
        added.copy(images = List()) should equal (imageList.copy(id = dbId, images = List()))
        added.images.map(_.src.value).sorted should equal (imageList.images.map(_.src.value).sorted)
        impl.delete(dbId).trRun()
      }
    }

    test(s"create find delete find ${impl.getClass.getSimpleName}") {
      forAll(Generators.ImageList.gen) { imageList =>
        val dbId = impl.create(imageList).trRun()
        impl.find(dbId).trRun() shouldBe defined
        impl.delete(dbId).trRun()
        impl.find(dbId).trRun() shouldBe None
      }
    }

    test(s"update ${impl.getClass.getSimpleName}") {
      def nonEmptyUpdate(u: ImageListUpdate): Boolean = u.productIterator.exists {
        case x: Option[_] => x.isDefined
        case _ =>  false
      }
      forAll(Generators.ImageList.update.filter(nonEmptyUpdate)) { update =>
        val imageList = Generators.ImageList.gen.sample.get

        val id = impl.create(imageList).trRun()
        impl.update(update.copy(id=id)).trRun()
        val updated = impl.get(id).trRun()
        update.displayName.foreach { newDisplayName =>
          updated.displayName should equal (newDisplayName)
        }
        update.images.foreach { newImages =>
          updated.images.map(_.src.value).toSet should equal (newImages.map(_.src.value).toSet)
        }

        impl.delete(id).trRun()
      }
    }
  }
}
