package ua.pomo.catalog.infrastructure.persistance

import cats.data.NonEmptyList
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.util.log.LogHandler
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image.ImageListSelector.IdsIn
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.infrastructure.persistance.ImageListRepositoryImpl.Queries
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

import java.util.UUID
import ua.pomo.catalog.infrastructure.persistance._

class ImageListRepositoryImplTest extends DbUnitTestSuite {
  private val postgres: ImageListRepository[ConnectionIO] = ImageListRepositoryImpl()
  private val inMemory: ImageListRepository[ConnectionIO] = InMemoryImageListRepositoryImpl()

  test("queries") {
    val id = ImageListId(UUID.randomUUID())
    val id2 = ImageListId(UUID.randomUUID())
    val displayName = ImageListDisplayName("qq1")
    check(Queries.findImageList(ImageListQuery(PageToken.NonEmpty(10, 5), IdsIn(NonEmptyList.of(id, id2)))))
    check(Queries.createImageList(displayName))
    check(Queries.createImages)
    forAll(Generators.ImageList.update) { update =>
      Queries.updateImageList(update).foreach(check(_))
    }
  }

  test("different transactions work") {
    implicit val logHandler = LogHandler.jdkLogHandler
    val imageListId = sql"insert into image_lists (display_name) values ('')".update
      .withUniqueGeneratedKeys[ImageListId]("id")
      .trRun()
    sql"select id from image_lists where id=$imageListId".query[ImageListId].option.trRun()
    sql"insert into images (src, alt, image_list_id) values ('','', $imageListId)".update.run.trRun()
  }

  test(s"create should work") {
    postgres.create(Generators.ImageList.gen.sample.get).trRun()
    println("""""")
  }

  Seq(postgres, inMemory).foreach { impl =>
    test(s"create get contract ${impl.getClass.getSimpleName}") {
      forAll(Generators.ImageList.gen) { imageList =>
        val dbId = impl.create(imageList).trRun()
        val added = impl.get(dbId).trRun()
        added.copy(images = List()) should equal(imageList.copy(id = dbId, images = List()))
        val id = ImageId(UUID.randomUUID())
        added.images.map(_.copy(id = id)).toSet should equal(imageList.images.map(_.copy(id = id)).toSet)
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
        case _            => false
      }
      forAll(Generators.ImageList.update.filter(nonEmptyUpdate)) { update =>
        val imageList = Generators.ImageList.gen.sample.get

        val id = impl.create(imageList).trRun()
        impl.update(update.copy(id = id)).trRun()
        val updated = impl.get(id).trRun()
        update.displayName.foreach { newDisplayName =>
          updated.displayName should equal(newDisplayName)
        }
        update.images.foreach { newImages =>
          updated.images.map(_.src.value).toSet should equal(newImages.map(_.src.value).toSet)
        }

        impl.delete(id).trRun()
      }
    }
  }
}
