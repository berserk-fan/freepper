package com.freepper.common.infrastructure.persistance.postgres

import cats.arrow.FunctionK
import cats.effect.{IO, Resource, Sync}
import cats.syntax.functor.toFunctorOps
import com.freepper.common.config.JdbcDatabaseConfig
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.freepper.common.domain.crud.{Crud, RepoOps, Repository}
import com.freepper.common.domain.registry.Registry
import com.freepper.common.domain.{Assertions, EntityTest, Fixture, Generators, Schema}
import com.freepper.common.{AppConfigLoader, DBMigrations, TransactorHelpers}

object EntityTestsHelpers {
  val welcomeResource: Resource[IO, Unit] = Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
    Sync[IO].blocking(println("Shutting down dbModuleTest"))
  )

  def inmemory[T <: Crud: ValueOf](
      r1: Registry.Aux[Repository, IO],
      r2: Registry[Generators],
      r3: Registry[Assertions],
      r4: Registry[RepoOps]
  ): Resource[IO, EntityTest[IO, IO, T]] = {
    for {
      _ <- EntityTestsHelpers.welcomeResource
    } yield EntityTest
      .ofRegistries[IO, IO](r1, r2, r3, r4, FunctionK.id[IO])
      .apply[T]
  }

  def postgres[T <: Crud: ValueOf: RepoOps](
      namespace: String,
      postgresRegistry: Registry.Aux[Repository, ConnectionIO],
      repoOps: Registry[RepoOps],
      assertions: Registry[Assertions],
      generators: Registry[Generators],
      fixtures: ConnectionIO[Registry[Fixture]]
  ): Resource[IO, EntityTest[ConnectionIO, IO, T]] = {
    for {
      _ <- welcomeResource
      crudName = RepoOps[T].entityDisplayName
      jdbc <- Resource
        .eval(AppConfigLoader.loadDefault[IO, JdbcDatabaseConfig](namespace, Some("jdbc")))
        .map { jdbc => jdbc.copy(schema = s"${crudName.value}-postgres-test-schema".toLowerCase) }
      l <- Resource.eval(Slf4jLogger.create[IO])
      trans = TransactorHelpers.fromConfig[IO](jdbc).trans
      _ <- Resource.make(DBMigrations.migrate[IO](jdbc)(implicitly, l).as(Schema())) { _ =>
        val res = sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(jdbc.schema)}" CASCADE;""".update.run.as(())
        trans.apply(res)
      }
      res <- Resource.eval(trans.apply {
        for {
          _ <- fixtures
          _ <- Sync[ConnectionIO].blocking(l.info("Executed migrations successfully"))

          postgresET = EntityTest
            .ofRegistries[ConnectionIO, IO](
              postgresRegistry,
              generators,
              assertions,
              repoOps,
              trans
            )
            .apply[T]
        } yield postgresET
      })
    } yield res
  }

  trait EntityTestsRegistry {
    def inmemory[T <: Crud: ValueOf]: Resource[IO, EntityTest[IO, IO, T]]

    def postgres[T <: Crud: ValueOf: RepoOps]: Resource[IO, EntityTest[ConnectionIO, IO, T]]
  }

  abstract class AbstractEntityTestsRegistry(
      namespace: String,
      crudOps: Registry[RepoOps],
      r2: Registry[Assertions],
      r3: Registry[Generators],
      inmem: IO[Registry.Aux[Repository, IO]],
      postgres: Registry.Aux[Repository, ConnectionIO],
      fixtures: ConnectionIO[Registry[Fixture]]
  ) extends EntityTestsRegistry {
    def inmemory[T <: Crud: ValueOf]: Resource[IO, EntityTest[IO, IO, T]] = {
      Resource
        .eval(inmem)
        .flatMap(x => EntityTestsHelpers.inmemory(x, r3, r2, crudOps))
    }

    def postgres[T <: Crud: ValueOf: RepoOps]: Resource[IO, EntityTest[ConnectionIO, IO, T]] = {
      EntityTestsHelpers.postgres(namespace, postgres, crudOps, r2, r3, fixtures)
    }
  }

}
