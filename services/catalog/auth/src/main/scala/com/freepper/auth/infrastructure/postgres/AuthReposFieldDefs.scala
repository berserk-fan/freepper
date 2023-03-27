package com.freepper.auth.infrastructure.postgres
import com.freepper.auth.domain.verification_token.VerificationTokenId
import com.freepper.common.infrastracture.persistance.postgres.DbFieldDef
import doobie.postgres.implicits.UuidType
import com.freepper.common.infrastracture.persistance.postgres.DoobieInstances

object AuthReposFieldDefs {
  import DoobieInstances.commonInstances._
  import DoobieInstances.timeInstances._
  import com.freepper.auth.domain.user._

  def gen[T: doobie.Read: doobie.Write](
      fieldName: String,
      isId: Boolean = false
  ): DbFieldDef[T] = {
    val isId1 = isId
    new DbFieldDef[T] {
      override def name: String = fieldName
      override def read: doobie.Read[T] = implicitly
      override def write: doobie.Write[T] = implicitly
      override def isId: Boolean = isId1
    }
  }

  implicit val UserUidFieldDef: DbFieldDef[UserUid] = gen("uid", isId = true)
  implicit val UserIdFieldDef: DbFieldDef[UserId] = gen("id", isId = true)
  implicit val UserDisplayNameFieldDef: DbFieldDef[UserDisplayName] = gen("display_name")
  implicit val UserEmailFieldDef: DbFieldDef[UserEmail] = gen("email")
  implicit val UserEmailVerificationTimeFieldDef: DbFieldDef[UserEmailVerificationTime] = {
    gen("email_verification_time_utc")
  }

  implicit val UserImageSrcFieldDef: DbFieldDef[UserImageSrc] = gen("image_src")

  // verification token
  implicit val VerificationTokenIdFieldDef: DbFieldDef[VerificationTokenId] = gen("id", isId = true)
}
