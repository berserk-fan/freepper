package com.freepper.common.domain

import cats.Traverse
import com.typesafe.config.{ConfigValue, ConfigValueFactory}
import pureconfig.ConfigReader
import pureconfig.error.{CannotConvert, ConfigReaderFailures, FailureReason}

import java.util.UUID

object auth {
  case class UserEmail(value: String)
  object UserEmail {
    implicit val q: ConfigReader[UserEmail] = ConfigReader.fromString(s => Right(UserEmail(s)))
  }

  sealed trait UserRole
  object UserRole {
    case object Admin extends UserRole
    case object User extends UserRole

    implicit val q: ConfigReader[UserRole] = ConfigReader.fromString {
      case "admin" => Right(UserRole.Admin)
      case "user"  => Right(UserRole.User)
      case x       => Left(CannotConvert(x, "UserRole", "it's not a user role"))
    }
  }

  case class User(email: UserEmail, role: UserRole)
  case class CallContext(user: Option[User])

  private implicit def userEmailListReader(implicit
      emailReader: ConfigReader[UserEmail]
  ): ConfigReader[List[UserEmail]] = {
    ConfigReader.fromString(s =>
      Traverse[List]
        .traverse[ConfigReader.Result[*], String, UserEmail](s.split(",").toList)(emailStr =>
          emailReader.from(ConfigValueFactory.fromAnyRef(emailStr))
        )
        .left
        .map(failures => CannotConvert(s, "List[UserEmail]", failures.prettyPrint()))
    )
  }

  case class AuthConfig(admins: List[UserEmail], jweSecret: String, sessionCookieName: String)
  object AuthConfig {
    implicit val r: ConfigReader[AuthConfig] =
      ConfigReader.forProduct3("admins", "jwe-secret", "session-cookie-name")(AuthConfig.apply)
  }

  case class CookieName(value: String)
  case class CookieValue(value: String)
  case class Cookie(name: CookieName, value: CookieValue)
}
