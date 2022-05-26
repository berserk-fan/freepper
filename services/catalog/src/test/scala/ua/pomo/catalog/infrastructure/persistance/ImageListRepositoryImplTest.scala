package ua.pomo.catalog.infrastructure.persistance

import cats.data.NonEmptyList
import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.image.ImageListSelector.IdsIn
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.infrastructure.persistance.ImageListRepositoryImpl.Queries
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}

import java.util.UUID

class ImageListRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution {
  override type Impl = ImageListRepository[ConnectionIO]
  override val resourcePerTest: Boolean = true
  case class TestResources(postgres: Impl, db: DbResources, impls: Seq[Impl]) extends HasDbResources with HasImpls
  override type Res = TestResources
  override val names = Seq("postgres", "inmemory")
  override def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      postgres <- Resource.pure(ImageListRepositoryImpl())
      inMemory <- Resource.pure(InMemoryImageListRepositoryImpl[ConnectionIO]()).mapK(db.xa.trans)
    } yield TestResources(postgres, db, Seq(postgres, inMemory))

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
    val imageListId = sql"insert into image_lists (display_name) values ('')".update
      .withUniqueGeneratedKeys[ImageListId]("id")
      .trRun()
    sql"select id from image_lists where id=$imageListId".query[ImageListId].option.trRun()
    sql"insert into images (src, alt, image_list_id, list_order) values ('','', $imageListId, 1)".update.run.trRun()
  }

  testR(s"create should work") { resources =>
    resources.postgres.create(Generators.ImageList.gen.sample.get).trRun()
  }

  testEachImpl(s"create get contract") { impl =>
    forAll(Generators.ImageList.gen) { imageList =>
      val dbId = impl.create(imageList).trRun()
      val added = impl.get(dbId).trRun()
      added.copy(images = List()) should equal(imageList.copy(id = dbId, images = List()))
      added.images should equal(imageList.images)
      impl.delete(dbId).trRun()
    }
  }

  testEachImpl(s"create find delete find") { impl =>
    forAll(Generators.ImageList.gen) { imageList =>
      val dbId = impl.create(imageList).trRun()
      impl.find(dbId).trRun() shouldBe defined
      impl.delete(dbId).trRun()
      impl.find(dbId).trRun() shouldBe None
    }
  }

  testEachImpl(s"update") { impl =>
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

  testR("empty the list") { res =>
    val imageList =
      ImageList(ImageListId(UUID.randomUUID()), ImageListDisplayName("qwe"), List(Image(ImageSrc(""), ImageAlt(""))))
    val imageListId = res.postgres.create(imageList).trRun()
    res.postgres.update(ImageListUpdate(imageListId, None, Some(List()))).trRun()
    res.postgres.get(imageListId).trRun().images should equal(List())
  }
}
