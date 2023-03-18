package com.freepper.auth.infrastructure.postgres

import com.freepper.auth.domain.{RegistryHelper, user}
import com.freepper.auth.domain.user.{
  CreateUserCommand,
  UserCrud,
  UserDisplayName,
  UserEmail,
  UserEmailVerificationTime,
  UserId,
  UserImageSrc,
  UserSelector,
  UserUid
}
import com.freepper.common.domain.registry.Registry
import com.freepper.common.domain.{Generators, crud}
import org.scalacheck.Gen

import java.time.Instant

object AuthGenerators {

  val registry: Registry[Generators] = RegistryHelper.createRegistry(UserGenerators)

  private object UserGenerators extends Generators[UserCrud] {
    private object PageToken {
      val nonEmpty: Gen[crud.PageToken.NonEmpty] = for {
        a <- Gen.posNum[Long]
        b <- Gen.posNum[Long]
      } yield crud.PageToken.NonEmpty(a, b)

      val gen: Gen[crud.PageToken] = Gen.oneOf(Gen.const(crud.PageToken.Empty), nonEmpty)
    }

    private val uid = Gen.uuid.map(UserUid.apply)
    private val id2_gen = Gen.uuid.map(_.toString).map(UserId.apply)
    private val idgen = Gen.either(id2_gen, uid)
    private val displayName = Gen.alphaNumStr.map(UserDisplayName.apply)
    private val email = Gen.alphaNumStr.map(_ + "@qq.com").map(UserEmail.apply)
    private val imageSrc = Gen.alphaNumStr.map(_ + ".qq.com/image.jpg").map(UserImageSrc.apply)
    private val emailVerificationTime = Gen.posNum[Long].map(Instant.ofEpochMilli).map(UserEmailVerificationTime.apply)

    override def create: Gen[user.CreateUserCommand] = for {
      id1 <- id2_gen
      dn <- displayName
      e <- email
      imageSrc1 <- Gen.option(imageSrc)
    } yield CreateUserCommand(id1, dn, e, imageSrc1)

    override def update: Gen[UserCrud#EntityId => user.UpdateUserCommand] = for {
      dn <- Gen.option(displayName)
      e <- Gen.option(email)
      evt <- Gen.option(Gen.option(emailVerificationTime))
      is <- Gen.option(Gen.option(imageSrc))
    } yield (id: UserCrud#EntityId) => user.UpdateUserCommand(id, dn, e, evt, is)

    override def genE: Gen[user.User] = for {
      i <- id2_gen
      u <- uid
      e <- email
      dn <- displayName
      evt <- Gen.option(emailVerificationTime)
      is <- Gen.option(imageSrc)
    } yield user.User(u, i, dn, e, evt, is)

    override def id: Gen[UserCrud#EntityId] = idgen

    override def query: Gen[crud.Query[user.UserSelector]] = for {
      si <- Gen.oneOf(
        id2_gen.map(UserSelector.IdIs.apply),
        uid.map(UserSelector.UidIs.apply),
        Gen.const(UserSelector.All)
      )
      p <- PageToken.nonEmpty
    } yield crud.Query(si, p)
  }
}
