package com.freepper.auth.domain

import com.freepper.common.domain.crud.{Crud, Repository, Service}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype

import java.time.Instant
import java.util.UUID

object verification_token {
  @derive(decoder)
  @newtype
  case class VerificationTokenUid(value: UUID)

  @derive(decoder)
  @newtype
  case class VerificationTokenId(value: String)

  @derive(decoder)
  @newtype
  case class VerificationTokenExpireTime(value: Instant)

  @derive(decoder)
  case class VerificationToken(
      uid: VerificationTokenUid,
      id: VerificationTokenId,
      expireTime: VerificationTokenExpireTime
  )

  case class CreateVerificationToken(
      uid: VerificationTokenUid,
      id: VerificationTokenId,
      expireTime: VerificationTokenExpireTime
  )
  
  case class UpdateVerificationToken(id: VerificationTokenId)
  
  sealed trait VerificationTokenSelector
  object VerificationTokenSelector {
    case class UidIs(uid: VerificationTokenUid) extends VerificationTokenSelector 
  }

  type VerificationTokenCrud = Crud.type
  type VerificationTokenRepository[F[_]] = Repository[F, VerificationTokenCrud]
  type VerificationTokenService[F[_]] = Service[F, VerificationTokenCrud]

  object Crud extends Crud {
    override type Create = CreateVerificationToken
    override type Update = UpdateVerificationToken
    override type Entity = VerificationToken
    override type EntityId = VerificationTokenId
    override type Selector = VerificationTokenSelector
  }
}
