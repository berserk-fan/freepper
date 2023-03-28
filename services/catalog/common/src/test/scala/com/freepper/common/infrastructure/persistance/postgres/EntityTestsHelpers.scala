package com.freepper.common.infrastructure.persistance.postgres

import cats.arrow.FunctionK
import cats.effect.{IO, Resource, Sync}
import cats.syntax.functor.toFunctorOps
import com.freepper.common.config.JdbcDatabaseConfig
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.freepper.common.domain.crud.{Crud, Repository}
import com.freepper.common.domain.{Assertions, EntityTest, Fixture, Generators, Schema, TypeName}
import com.freepper.common.{AppConfigLoader, DBMigrations, TransactorHelpers}
import org.typelevel.log4cats.Logger

object EntityTestsHelpers {
  val welcomeResource: Resource[IO, Unit] = Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
    Sync[IO].blocking(println("Shutting down dbModuleTest"))
  )

  def inmemory[C[_]](
      r1: Repository[IO, C],
      r2: Generators[C],
      r3: Assertions[C]
  ): Resource[IO, EntityTest[IO, IO, C]] = {
    for {
      _ <- EntityTestsHelpers.welcomeResource
    } yield EntityTest(r1, r2, r3, FunctionK.id[IO])
  }

  def postgres[C[_]](
      namespace: String,
      postgresRegistry: Repository[ConnectionIO, C],
      assertions: Assertions[C],
      generators: Generators[C],
      fixtures: ConnectionIO[Fixture[C]],
      typeName: TypeName[C]
  ): Resource[IO, EntityTest[ConnectionIO, IO, C]] = {
    for {
      _ <- welcomeResource
      crudName = typeName.name
      jdbc <- Resource
        .eval(AppConfigLoader.loadDefault[IO, JdbcDatabaseConfig](namespace, Some("jdbc")))
        .map { jdbc => jdbc.copy(schema = s"${crudName}-postgres-test-schema".toLowerCase) }
      given Logger[IO] <- Resource.eval(Slf4jLogger.create[IO])
      trans = TransactorHelpers.fromConfig[IO](jdbc).trans
      _ <- Resource.make(DBMigrations.migrate[IO](jdbc).as(Schema())) { _ =>
        val res = sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(jdbc.schema)}" CASCADE;""".update.run.as(())
        trans.apply(res)
      }
      res <- Resource.eval(trans.apply {
        for {
          _ <- fixtures
          _ <- Sync[ConnectionIO].blocking(Logger[IO].info("Executed migrations successfully"))

          postgresET = EntityTest(postgresRegistry, generators, assertions, trans)
        } yield postgresET
      })
    } yield res
  }
}
