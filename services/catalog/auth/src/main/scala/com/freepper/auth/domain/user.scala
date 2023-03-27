package com.freepper.auth.domain

import cats.Id
import com.freepper.common.domain.{TypeName, crud}
import com.freepper.common.domain.crud.Crud
import com.freepper.common.infrastracture.persistance.{GenericSelector, ValueK}
import derevo.derive
import io.estatico.newtype.macros.newtype
import derevo.circe.magnolia.decoder
import monocle.macros.Lenses

import java.time.Instant
import java.util.UUID

object user {

  @derive(decoder)
  @newtype
  case class UserUid(value: UUID)
  @derive(decoder)
  @newtype
  case class UserId(value: String)
  @derive(decoder)
  @newtype
  case class UserDisplayName(value: String)
  @derive(decoder)
  @newtype
  case class UserEmail(value: String)
  @derive(decoder)
  @newtype
  case class UserEmailVerificationTime(value: Instant)
  @derive(decoder)
  @newtype
  case class UserImageSrc(value: String)

  @derive(decoder)
  @Lenses
  case class User(
      uid: UserUid,
      id: UserId,
      displayName: UserDisplayName,
      email: UserEmail,
      emailVerificationTime: Option[UserEmailVerificationTime],
      imageSrc: Option[UserImageSrc]
  )

  @Lenses
  case class CreateUserCommand(
      id: UserId,
      displayName: UserDisplayName,
      email: UserEmail,
      imageSrc: Option[UserImageSrc]
  )

  @Lenses
  case class UpdateUserCommand(
      selector: GenericSelector[UserDomainField],
      displayName: Option[UserDisplayName],
      email: Option[UserEmail],
      emailVerificationTime: Option[Option[UserEmailVerificationTime]],
      imageSrc: Option[Option[UserImageSrc]]
  )

  type UserSelector = GenericSelector[UserDomainField]
  type UserCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateUserCommand
    override type Update = UpdateUserCommand
    override type Entity = User
    override type EntityId = UserId
    override type Selector = UserSelector

    implicit val show: TypeName[this.type] = new TypeName[this.type] {
      override def name: String = "user"
    }
  }
}
