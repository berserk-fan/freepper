package ua.pomo.catalog.infrastructure.persistance.s3

import cats.effect.{IO, Resource}
import doobie.util.testing.UnsafeRun
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import ua.pomo.catalog.domain.image.{CreateImageData, ImageData, ImageDataRepository, ImageSrc}
import ua.pomo.catalog.shared.{ForEachImpl, Resources}
import ua.pomo.common.{HasResource, TestIORuntime}

class S3ImageDataRepositoryImplEvil
    extends AnyFunSuite
    with Matchers
    with HasResource[IO]
    with ForEachImpl
    with TestIORuntime {
  case class TestRes(impls: Seq[(String, ImageDataRepository[IO])])
  override type TestResource = TestRes
  override type Impl = ImageDataRepository[IO]
  override def getImpls(resources: TestRes): Seq[(String, Impl)] = resources.impls
  override def names: Seq[String] = Seq("s3", "inmemory")

  override def runResource[T](r: cats.effect.IO[T]): T = r.unsafeRunSync()(runtime)

  override protected def resource: Resource[IO, TestRes] = for {
    appConfig <- Resources.config
    s3Repo <- Resource.eval {
      implicit val f: LoggerFactory[IO] = Slf4jFactory[IO]
      S3ImageDataRepository[IO](appConfig.aws)
    }
    inMemoryRepo <- Resource.eval(InMemoryImageDataRepository())
  } yield TestRes(Seq(("s3", s3Repo), ("inmemory", inMemoryRepo)))

  testEachImplR("create delete contract")((_, impl) => {
    val imageData = getClass.getResourceAsStream("/kitty.webp").readAllBytes()
    val src = ImageSrc("some/folder/kitty.webp")
    val img = CreateImageData(src, ImageData(imageData))
    impl.list("").unsafeRunSync() shouldBe empty
    impl.create(img).unsafeRunSync()
    impl.list("").unsafeRunSync() should equal(List(src))
    impl.list("some").unsafeRunSync() should equal(List(src))
    impl.list("some/folder").unsafeRunSync() should equal(List(src))
    impl.list("some/other-folder").unsafeRunSync() shouldBe empty
    impl.delete(src).unsafeRunSync()
    impl.list("").unsafeRunSync() shouldBe empty
  })

  def monadCancelThrow: cats.effect.MonadCancelThrow[cats.effect.IO] = implicitly
  def unsafeRun: UnsafeRun[cats.effect.IO] = new UnsafeRun[IO] {
    override def unsafeRunSync[A](fa: IO[A]): A = fa.unsafeRunSync()
  }
}
