package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits._
import ua.pomo.common.domain.error.NotFound
import ua.pomo.catalog.domain.image.{CreateImageMetadata, ImageAlt, ImageId, ImageRepository, ImageSelector, ImageSrc}
import ua.pomo.catalog.shared.{ForEachImpl, Generators, Resources}
import ua.pomo.common.{DbResources, DbUnitTestSuite}
import ua.pomo.common.domain.repository.{PageToken, Query}
import ua.pomo.common.UnsafeRunnable._
import ua.pomo.common.infrastracture.persistance.RepositoryK

import java.util.UUID

class ImageRepositoryImplTest extends DbUnitTestSuite with ForEachImpl {
  override type Impl = ImageRepository[IO]
  case class TestResources(db: DbResources, impls: Seq[(String, Impl)])
  override protected type TestResource = TestResources
  override val resourcePerTest: Boolean = true

  override def getDbResources(resources: TestResource): DbResources = resources.db
  override def getImpls(resources: TestResource): Seq[(String, Impl)] = resources.impls
  override def names: Seq[String] = Seq("postgres")

  override protected def resource: Resource[IO, TestResource] = for {
    db <- Resources.dbTest
  } yield TestResources(db, Seq(("postgres", RepositoryK(ImageRepositoryImpl, db.xa.trans))))

  test("queries") {
    val imageId = ImageId(UUID.randomUUID())
    ImageQueries.create(CreateImageMetadata(ImageSrc(""), ImageAlt("")))
    ImageQueries.create(CreateImageMetadata(ImageSrc("q/w/e"), ImageAlt("xxx xxx")))
    ImageQueries.delete(imageId)
    ImageQueries.find(Query(ImageSelector.All, PageToken.NonEmpty(1, 1)))
    ImageQueries.find(Query(ImageSelector.All, PageToken.NonEmpty(1, 0)))
    ImageQueries.find(Query(ImageSelector.IdIs(imageId), PageToken.NonEmpty(1, 0)))
  }

  testEachImpl("create get delete") { impl =>
    forAll(Generators.Image.create) { cr =>
      val id = impl.create(cr).trRun()
      val added = impl.get(id).trRun()
      cr.src should equal(added.src)
      cr.alt should equal(added.alt)
      impl.delete(id).trRun()
      intercept[Exception] {
        impl.get(id).trRun()
      }
    }
  }

  testEachImpl("get failure should return NotFound") { impl =>
    intercept[NotFound] {
      impl.get(ImageId(UUID.randomUUID())).trRun()
    }
  }

  testEachImpl("delete count") { impl =>
    impl.delete(ImageId(UUID.randomUUID())).trRun() should equal(0)
    val id = impl.create(Generators.Image.create.sample.get).trRun()
    val id2 = impl.create(Generators.Image.create.sample.get).trRun()
    impl.delete(id).trRun() should equal(1)
    impl.delete(id2).trRun() should equal(1)
  }
}
