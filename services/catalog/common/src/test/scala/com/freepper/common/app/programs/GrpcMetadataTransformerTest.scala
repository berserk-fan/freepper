package com.freepper.common.app.programs

import io.grpc.Metadata
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.freepper.common.domain.auth.{Cookie, CookieName, CookieValue}

import scala.jdk.CollectionConverters._
import scala.util.Try

class GrpcMetadataTransformerTest extends AnyFunSuite with Matchers {
  test("const should change nothing") {
    val m = new Metadata()
    val t = GrpcMetadataTransformer.const[Try]
    val res = t.transform(m).get
    res.keys().asScala should ===(Set())
  }

  test("extract header") {
    val authCookie = "next-auth.session-token"
    val authCookieValue = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..L-Fw_o"
    val cookieParser = CookieParser.const[Try](Cookie(CookieName(authCookie), CookieValue(authCookieValue)))

    intercept[IllegalArgumentException] {
      GrpcMetadataTransformer.cookieToMetadata[Try](cookieParser, CookieName(authCookie), "Uppercase key").get
    }
    intercept[IllegalArgumentException] {
      GrpcMetadataTransformer.cookieToMetadata[Try](cookieParser, CookieName(authCookie), "").get
    }

    val metadataAuthKey = "authorization"
    val t = GrpcMetadataTransformer.cookieToMetadata[Try](cookieParser, CookieName(authCookie), metadataAuthKey).get
    val res = t.transform(new Metadata()).get
    res.keys().asScala should ===(Set("authorization"))
    res.get(Metadata.Key.of(metadataAuthKey, Metadata.ASCII_STRING_MARSHALLER)) should ===(authCookieValue)
  }
}
