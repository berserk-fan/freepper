package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.arrow.FunctionK
import cats.effect.{IO, Resource, Sync}
import cats.syntax.functor.toFunctorOps
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ua.pomo.catalog.AppConfig
import ua.pomo.catalog.domain.Registry
import ua.pomo.catalog.domain.Registry._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image.ImageCrud
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.catalog.shared.FixturesV2
import ua.pomo.common.domain.repository.{Crud, CrudOps}
import ua.pomo.common.domain.{EntityTest, Schema}
import ua.pomo.common.{AppConfigLoader, DBMigrations, TransactorHelpers}

object CatalogEntityTests {
  private val welcomeResource = Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
    Sync[IO].blocking(println("Shutting down dbModuleTest"))
  )

  private val crudOps = new Registry[CrudOps] {
    override def category: CrudOps[CategoryCrud] = implicitly
    override def image: CrudOps[ImageCrud] = implicitly
    override def imageList: CrudOps[ImageListCrud] = implicitly
    override def model: CrudOps[ModelCrud] = implicitly
    override def product: CrudOps[ProductCrud] = implicitly
    override def parameterList: CrudOps[ParameterListCrud] = implicitly
  }

  def inmemory[T <: Crud: ValueOf]: Resource[IO, EntityTest[IO, IO, T]] = {
    for {
      _ <- welcomeResource
      inMemoryRepos <- Resource.eval(inMemoryRepoRegistry[IO])
      generatorsRegistry = DbGenerators.generatorRegistry

      inMemoryET = EntityTest
        .ofRegistries[IO, IO](
          inMemoryRepos.toUntyped,
          generatorsRegistry.toUntyped,
          CatalogAssertions.registry.toUntyped,
          crudOps.toUntyped,
          FunctionK.id[IO]
        )
        .apply[T]
    } yield inMemoryET
  }

  def postgres[T <: Crud: ValueOf: CrudOps]: Resource[IO, EntityTest[ConnectionIO, IO, T]] = {
    for {
      _ <- welcomeResource
      crudName = CrudOps[T].entityDisplayName
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
              postgresRepos.toUntyped,
              generatorsRegistry.toUntyped,
              CatalogAssertions.registry.toUntyped,
              crudOps.toUntyped,
              trans
            )
            .apply[T]
        } yield postgresET
      })
    } yield res
  }
}
