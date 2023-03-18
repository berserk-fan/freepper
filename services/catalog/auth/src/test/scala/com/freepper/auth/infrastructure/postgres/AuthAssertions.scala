package com.freepper.auth.infrastructure.postgres

import com.freepper.auth.domain.{RegistryHelper, user}
import com.freepper.auth.domain.user.UserCrud
import org.scalatest.matchers.should.Matchers
import com.freepper.common.domain.Assertions
import com.freepper.common.domain.registry.Registry

object AuthAssertions extends Matchers {
  private val userCheckers: Assertions[UserCrud] = new Assertions[UserCrud] {
    override def update(c: user.UpdateUserCommand, v: user.User): Any = {
      c.id === v.id
      c.displayName.foreach(_ === v.displayName)
      c.email.foreach(_ === v.email)
      c.imageSrc.foreach(_ === v.imageSrc)
      c.emailVerificationTime.foreach(_ === v.emailVerificationTime)
    }

    override def create(c: user.CreateUserCommand, v: user.User): Any = {
      c.email === v.email
      c.imageSrc === v.imageSrc
      c.id === v.id
      c.displayName === v.displayName
    }
  }

  val registry: Registry[Assertions] = RegistryHelper.createRegistry(userCheckers)
}
