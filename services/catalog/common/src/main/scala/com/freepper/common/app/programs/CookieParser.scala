package com.freepper.common.app.programs

import cats.{Applicative, ApplicativeThrow}
import io.grpc.Metadata
import com.freepper.common.domain.auth.{Cookie, CookieName, CookieValue}

trait CookieParser[F[_]] {
  def parse(m: Metadata): F[List[Cookie]]
}

object CookieParser {
  def const[F[_]: Applicative](cookies: Cookie*): CookieParser[F] = (_: Metadata) => Applicative[F].pure(cookies.toList)

  def fromKey[F[_]: ApplicativeThrow](key: String): CookieParser[F] = (m: Metadata) => {
    ApplicativeThrow[F].catchNonFatal {
      val k = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)
      val cookieHeader = Option(m.get(k))
      cookieHeader.fold(List[Cookie]()) {
        _.split(";")
          .map(cookie => {
            val name :: value :: Nil = cookie.trim.split("=").toList
            Cookie(CookieName(name), CookieValue(value))
          })
          .toList
      }
    }
  }
}
