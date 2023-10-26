package com.freepper.catalog.infrastructure.persistance.postgres

import cats.effect.IO
import com.freepper.catalog.shared.FixturesV2
import .RepoOps
import com.freepper.common.infrastructure.persistance.postgres.EntityTestsHelpers.AbstractEntityTestsRegistry
import doobie.ConnectionIO
import org.typelevel.log4cats.slf4j.loggerFactoryforSync

object CatalogEntityTests
    extends AbstractEntityTestsRegistry(
      "catalog",
      RegistryHelper.usingImplicits[RepoOps],
      CatalogAssertions.registry,
      CatalogGenerators.registry,
      inMemoryRepoRegistry[IO],
      postgresRepoRegistry,
      FixturesV2.fixtureRegistry(postgresRepoRegistry)
    )
