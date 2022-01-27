package ua.pomo.catalog.domain

object error {
  sealed abstract class Err(msg: String, cause: Option[Throwable]) extends Exception(msg, cause.orNull)
  case class ValidationErr(msg: String, cause: Option[Throwable] = None) extends Err(msg, cause)
  case class NotFound(msg: String, cause: Option[Throwable] = None) extends Err(msg, cause)
  case class DbErr(msg: String, cause: Option[Throwable]) extends Err(msg,cause)
}
