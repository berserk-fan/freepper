package com.freepper.auth.infrastructure.postgres

import com.freepper.common.infrastructure.persistance.postgres.AbstractIORepositoryTest
import cats.effect.IO
import com.freepper.auth.domain.user.UserCrud
import org.typelevel.log4cats.slf4j.loggerFactoryforSync
import doobie.ConnectionIO

class UserRepositoryPostgresTest()
    extends AbstractIORepositoryTest[ConnectionIO, UserCrud](AuthEntityTest.postgres[UserCrud])

class UserRepositoryInmemoryTest() extends AbstractIORepositoryTest[IO, UserCrud](AuthEntityTest.inmemory[UserCrud])
