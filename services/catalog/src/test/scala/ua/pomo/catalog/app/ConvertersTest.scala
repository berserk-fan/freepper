package ua.pomo.catalog.app

import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import ua.pomo.catalog.app.Converters.PageTokenStr
import ua.pomo.catalog.shared.Generators

class ConvertersTest extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers with EitherValues {
  test("pageToken") {
    forAll(Generators.PageToken.self) { pageToken =>
      Converters.toDomain(PageTokenStr(Converters.toApi(pageToken))).toEither.value should equal(pageToken)
    }
  }
}
