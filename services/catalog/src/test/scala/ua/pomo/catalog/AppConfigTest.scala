package ua.pomo.catalog

import cats.effect.IO
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.shared.HasIORuntime

import java.io.File
import java.nio.file.Files

class AppConfigTest extends AnyFlatSpec with Matchers with EitherValues with HasIORuntime {
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
