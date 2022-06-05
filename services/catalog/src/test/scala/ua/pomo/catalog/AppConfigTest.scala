package ua.pomo.catalog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.File
import java.nio.file.Files

class AppConfigTest extends AnyFlatSpec with Matchers with EitherValues {
  "AppConfig" should "fail default path" in {
    noException should be thrownBy AppConfig.loadDefault[IO].unsafeRunSync()
  }
}

object AppConfigTest {
  def mockFile(fileContents: String): File = {
    val file = File.createTempFile("tmpTestDir", s"application.conf")
    Files.writeString(file.toPath, fileContents)
    file
  }
}
