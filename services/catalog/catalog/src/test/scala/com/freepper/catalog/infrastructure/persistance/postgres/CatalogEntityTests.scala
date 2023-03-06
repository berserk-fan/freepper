package com.freepper.catalog.infrastructure.persistance.postgres

import cats.arrow.FunctionK
import cats.effect.{IO, Resource, Sync}
import cats.syntax.functor.toFunctorOps
import com.freepper.catalog.shared.FixturesV2
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.freepper.catalog.AppConfig
import com.freepper.catalog.domain.RegistryHelper
import com.freepper.catalog.domain.RegistryHelper._
import com.freepper.common.domain.crud.{Crud, RepoOps}
import com.freepper.common.domain.{EntityTest, Schema}
import com.freepper.common.{AppConfigLoader, DBMigrations, TransactorHelpers}

object CatalogEntityTests {
  private val welcomeResource = Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
    Sync[IO].blocking(println("Shutting down dbModuleTest"))
  )

  private val crudOps = RegistryHelper.usingImplicits[RepoOps]

  def inmemory[T <: Crud: ValueOf]: Resource[IO, EntityTest[IO, IO, T]] = {
    for {
      _ <- welcomeResource
      inMemoryRepos <- Resource.eval(inMemoryRepoRegistry[IO])
      generatorsRegistry = DbGenerators.generatorRegistry

      inMemoryET = EntityTest
        .ofRegistries[IO, IO](
          inMemoryRepos,
          generatorsRegistry,
          CatalogAssertions.registry,
          crudOps,
          FunctionK.id[IO]
        )
        .apply[T]
    } yield inMemoryET
  }

  def postgres[T <: Crud: ValueOf: RepoOps]: Resource[IO, EntityTest[ConnectionIO, IO, T]] = {
    for {
      _ <- welcomeResource
      crudName = RepoOps[T].entityDisplayName
      config <- Resource.eval(AppConfigLoader.loadDefault[IO, AppConfig]("catalog")).map { cfg =>
        cfg.copy(jdbc = cfg.jdbc.copy(schema = s"${crudName.value}-postgres-test-schema".toLowerCase))
      }
      l <- Resource.eval(Slf4jLogger.create[IO])
      trans = TransactorHelpers.fromConfig[IO](config.jdbc).trans
      _ <- Resource.make(DBMigrations.migrate[IO](config.jdbc)(implicitly, l).as(Schema())) { _ =>
        val res = sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(config.jdbc.schema)}" CASCADE;""".update.run.as(())
        trans.apply(res)
      }
      res <- Resource.eval(trans.apply {
        for {
          l2 <- Slf4jLogger.create[ConnectionIO]
          postgresRepos = postgresRepoRegistry
          generatorsRegistry = DbGenerators.generatorRegistry
          _ <- {
            implicit val v2: Logger[ConnectionIO] = l2
            FixturesV2.fixtureRegistry(postgresRepos)
          }
          _ <- Sync[ConnectionIO].blocking(l.info("Executed migrations successfully"))

          postgresET = EntityTest
            .ofRegistries[ConnectionIO, IO](
              postgresRepos,
              generatorsRegistry,
              CatalogAssertions.registry,
              crudOps,
              trans
            )
            .apply[T]
        } yield postgresET
      })
    } yield res
  }
}
