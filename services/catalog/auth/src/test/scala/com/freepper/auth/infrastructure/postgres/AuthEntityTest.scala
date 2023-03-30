package com.freepper.auth.infrastructure.postgres

import cats.effect.IO
import com.freepper.auth.domain.RegistryHelper
import com.freepper.common.infrastructure.persistance.postgres.EntityTestsHelpers.AbstractEntityTestsRegistry
import doobie.ConnectionIO
import org.typelevel.log4cats.slf4j.loggerFactoryforSync

object AuthEntityTest
    extends AbstractEntityTestsRegistry(
      "auth",
      RegistryHelper.usingImplicits[RepoOps],
      AuthAssertions.registry,
      AuthGenerators.registry,
      RepoRegistry.inmemory[IO],
      RepoRegistry.postgres,
      AuthFixtures.fixtureRegistry(RepoRegistry.postgres)
    )
