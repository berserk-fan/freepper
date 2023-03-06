package com.freepper.common.app.programs

import io.grpc.Metadata
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.freepper.common.domain.auth.{Cookie, CookieName, CookieValue}

import scala.util.Try

class CookieParserTest extends AnyFunSuite with Matchers {

  test("const") {
    CookieParser.const[Try](Cookie(CookieName("hello"), CookieValue("world"))).parse(new Metadata()).get should ===(
      List(Cookie(CookieName("hello"), CookieValue("world")))
    )
  }

  test("fromKey") {
    val authCookie = "next-auth.session-token"
    val authCookieValue = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..L-Fw_o"
    val cookie =
      s"next-auth.csrf-token=sometoken; next-auth.callback-url=http%3A%2F%2Flocalhost%3A3000; $authCookie=$authCookieValue"

    val m = new Metadata()
    m.put(Metadata.Key.of("cookie", Metadata.ASCII_STRING_MARSHALLER), cookie)
    CookieParser.fromKey[Try]("cookie").parse(m).get.size should ===(3)
  }

}
