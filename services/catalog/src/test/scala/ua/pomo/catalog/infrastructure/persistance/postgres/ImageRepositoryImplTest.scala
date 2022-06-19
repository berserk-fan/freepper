package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image.{
  CreateImageMetadata,
  ImageAlt,
  ImageId,
  ImageQuery,
  ImageRepository,
  ImageSelector,
  ImageSrc
}
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, Resources}

import java.util.UUID

class ImageRepositoryImplTest extends DbUnitTestSuite {
  override type Impl = ImageRepository[ConnectionIO]
  case class TestResources(db: DbResources, impls: Seq[(String, Impl)])
  override protected type TestResource = TestResources
  override val resourcePerTest: Boolean = true

  override def getDbResources(resources: TestResource): DbResources = resources.db
  override def getImpls(resources: TestResource): Seq[(String, Impl)] = resources.impls
  override def names: Seq[String] = Seq("postgres")

  override protected def resource: Resource[IO, TestResource] = for {
    db <- Resources.dbTest
  } yield TestResources(db, Seq(("postgres", ImageRepositoryImpl)))

  test("queries") {
    val imageId = ImageId(UUID.randomUUID())
    ImageRepositoryImpl.Queries.create(CreateImageMetadata(ImageSrc(""), ImageAlt("")))
    ImageRepositoryImpl.Queries.create(CreateImageMetadata(ImageSrc("q/w/e"), ImageAlt("xxx xxx")))
    ImageRepositoryImpl.Queries.get(imageId)
    ImageRepositoryImpl.Queries.delete(imageId)
    ImageRepositoryImpl.Queries.query(ImageQuery(ImageSelector.All, PageToken.NonEmpty(1, 1)))
    ImageRepositoryImpl.Queries.query(ImageQuery(ImageSelector.All, PageToken.NonEmpty(1, 0)))
    ImageRepositoryImpl.Queries.query(ImageQuery(ImageSelector.IdIs(imageId), PageToken.NonEmpty(1, 0)))
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
