package com.freepper.common.app.programs

import cats.effect.IO
import cats.{Applicative, ApplicativeThrow, MonadThrow}
import io.grpc.Metadata
import cats.syntax.functor.toFunctorOps
import cats.syntax.flatMap.toFlatMapOps
import com.freepper.common.domain.auth.CookieName

trait GrpcMetadataTransformer[F[_]] {
  def transform(m: Metadata): F[Metadata]
}

object GrpcMetadataTransformer {
  def const[F[_]: Applicative]: GrpcMetadataTransformer[F] = { (m: Metadata) =>
    Applicative[F].pure(m)
  }

  /* Optionally extracts cookie into metadata key*/
  def cookieToMetadata[F[_]: MonadThrow](
      cookieParser: CookieParser[F],
      cookieName: CookieName,
      metadataKey: String
  ): F[GrpcMetadataTransformer[F]] = {
    if (metadataKey.toLowerCase != metadataKey || metadataKey.isBlank) {
      MonadThrow[F].raiseError(
        new IllegalArgumentException(
          s"metadata key should be lowercase nonempty string but was(quotes excluded): '$metadataKey'"
        )
      )
    } else {
      MonadThrow[F].pure { (m: Metadata) =>
        {
          for {
            cookies <- cookieParser.parse(m)
            mcopy = new Metadata()
            _ = mcopy.merge(m)
            key = Metadata.Key.of(metadataKey, Metadata.ASCII_STRING_MARSHALLER)
            _ = cookies.find(_.name == cookieName).foreach { cookie =>
              mcopy.put(key, cookie.value.value)
            }
          } yield mcopy
        }
      }
    }
  }

  def cookieAuthExtractor[F[_]: MonadThrow](sessionCookieName: String): F[GrpcMetadataTransformer[F]] =
    GrpcMetadataTransformer.cookieToMetadata[F](
      CookieParser.fromKey[F]("cookie"),
      CookieName(sessionCookieName),
      "authorization"
    )
}
