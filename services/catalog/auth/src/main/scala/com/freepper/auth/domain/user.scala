package com.freepper.auth.domain

import cats.Id
import com.freepper.common.domain.crud
import .Crud
import com.freepper.common.infrastracture.persistance.{GenericSelector, ValueK}



import monocle.macros.Lenses

import java.time.Instant
import java.util.UUID

object user {



  case class UserUid(value: UUID)


  case class UserId(value: String)


  case class UserDisplayName(value: String)


  case class UserEmail(value: String)


  case class UserEmailVerificationTime(value: Instant)


  case class UserImageSrc(value: String)


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
