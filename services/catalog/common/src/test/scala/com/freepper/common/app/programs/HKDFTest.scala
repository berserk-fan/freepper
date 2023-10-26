package com.freepper.common.app.programs

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.freepper.common.TestIORuntime
import cats.effect.IO

import java.util.Base64

class HKDFTest extends AnyFunSuite with Matchers with TestIORuntime {
  test("basic hkdf") {
    val res = Base64.getEncoder.encodeToString(HKDF.hkdf[IO]("hello", "some salt", "some info", 32).unsafeRunSync())
    res.should(===("Ar3ttgR7H6S6IkbyXRq8NWwKolvNcd7DGfFoK3nUTJA="))

    val key = "k+Twz5BFK8s/Y728OHqPQzgUzadi+tdRVsjzY6wLDwk="
    val res2 =
      Base64.getEncoder.encodeToString(HKDF.hkdf[IO](key, "", "Auth.js Generated Encryption Key", 32).unsafeRunSync())
    res2.should(===("u0kp09pLbuWlRQwQ7w5/8JeiTnjmWr+CNwoKHP+3GCM="))
  }
}
