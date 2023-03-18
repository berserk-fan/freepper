package com.freepper.auth.domain

import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, EntityDisplayName, RepoOps}
import com.freepper.common.infrastracture.persistance.GenericSelectorModule
import com.freepper.common.infrastracture.persistance.postgres.QueryHelpers.Test.GenericSelector
import derevo.derive
import io.estatico.newtype.macros.newtype
import derevo.circe.magnolia.decoder
import monocle.macros.Lenses

import java.time.Instant
import java.util.UUID

object user {

  sealed trait UserDomainField
  @derive(decoder)
  @newtype
  case class UserUid(value: UUID) extends UserDomainField
  @derive(decoder)
  @newtype
  case class UserId(value: String) extends UserDomainField
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

  case class CreateUserCommand(
      id: UserId,
      displayName: UserDisplayName,
      email: UserEmail,
      imageSrc: Option[UserImageSrc]
  )

  case class UpdateUserCommand(
      id: Either[UserId, UserUid],
      displayName: Option[UserDisplayName],
      email: Option[UserEmail],
      emailVerificationTime: Option[Option[UserEmailVerificationTime]],
      imageSrc: Option[Option[UserImageSrc]]
  )

  type UserCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateUserCommand
    override type Update = UpdateUserCommand
    override type Entity = User
    override type EntityId = Either[UserId, UserUid]

    object UserGenericSelector extends GenericSelectorModule {
      override type DomainField = this.type
    }
    override type Selector = GenericSelector

    implicit val qq: RepoOps[this.type] = new RepoOps[this.type] {
      override def getIdUpdate(update: Update): EntityId = update.id
      override def getIdCreate(create: Create): EntityId = Left(create.id)
      override def getIdEntity(entity: Entity): EntityId = Left(entity.id)
      override def entityDisplayName: crud.EntityDisplayName = EntityDisplayName("user")
    }
  }
}
