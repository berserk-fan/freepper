package com.freepper.common.app

import cats.{ApplicativeError, ApplicativeThrow}
import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, catsSyntaxMonadError}
import io.grpc.Status
import org.typelevel.log4cats.LoggerFactory
import com.freepper.common.domain.error.{DbErr, ImpureError, NotFound, ValidationErr}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.syntax.either.catsSyntaxEither
import cats.syntax.applicativeError.catsSyntaxApplicativeError
import scalapb.validate.{Failure, Success, Validator}

import scala.util.Try

object CommonServiceMethods {

  def adaptError[F[_]: LoggerFactory: Async, T](f: => F[T]): F[T] = for {
    logger <- LoggerFactory[F].create
    res <- Async[F]
      .fromEither(Try(f).toEither.leftMap(err => ImpureError("Api method has thrown an error: impure", Some(err))))
      .flatten
      .onError {
        case e: ValidationErr => logger.info(e)("Api method: Validation request error")
        case e: NotFound      => logger.info(e)("Api method: Not found error")
        case e                => logger.error(e)("Api method: Severe error")
      }
      .adaptError { e =>
        val status = e match {
          case ValidationErr(_, _) => Status.INVALID_ARGUMENT
          case DbErr(_, _)         => Status.INTERNAL
          case NotFound(_, _, _)   => Status.NOT_FOUND
          case _                   => Status.INTERNAL
        }
        status.withDescription(s"${e.getClass.getSimpleName} ${e.getMessage}").withCause(e).asException()
      }
  } yield res

  def validate[F[_]: ApplicativeThrow, T: Validator](t: T): F[Unit] = Validator[T].validate(t) match {
    case Success => ().pure[F]
    case Failure(violations) =>
      ValidationErr(violations.mkString(",")).raiseError[F, Unit]
  }
}
