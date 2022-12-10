package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.arrow.FunctionK
import cats.effect.{IO, Resource, Sync}
import cats.syntax.functor.toFunctorOps
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment}
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ua.pomo.catalog.AppConfig
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image.{BuzzImageUpdate, CreateImageMetadata, Image, ImageCrud}
import ua.pomo.catalog.infrastructure.persistance.postgres.DbGenerators.{CategoryGenerators, ImageGenerators}
import ua.pomo.catalog.shared.FixturesV2.{CategoryFixture, ImageFixture}
import ua.pomo.common.domain.{Assertions, EntityTest, Schema}
import ua.pomo.common.{AppConfigLoader, DBMigrations, TransactorHelpers}

import java.util.UUID

object DbModuleTest extends Matchers {

  def categoryPostgres: CatalogRepositoryAbstractTest.SuiteResource[CategoryCrud] = allModules.map(_._1)
  def categoryInMemory: CatalogRepositoryAbstractTest.SuiteResource[CategoryCrud] = allModules.map(_._2)
  def imagePostgres: CatalogRepositoryAbstractTest.SuiteResource[ImageCrud] = allModules.map(_._3)

  private def allModules = {
    for {
      _ <- Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
        Sync[IO].blocking(println("Shutting down dbModuleTest"))
      )
      config <- Resource.eval(AppConfigLoader.loadDefault[IO, AppConfig]("catalog")).map { cfg =>
        cfg.copy(jdbc = cfg.jdbc.copy(schema = UUID.randomUUID.toString))
      }

      l <- Resource.eval(Slf4jLogger.create[IO])
      trans = TransactorHelpers.fromConfig[IO](config.jdbc).trans

      _ <- Resource.make(DBMigrations.migrate[IO](config.jdbc)(implicitly, l).as(Schema())) { _ =>
        val res = sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(config.jdbc.schema)}" CASCADE;""".update.run.as(())
        trans.apply(res)
      }

      res <- Resource.eval(trans.apply {
        for {
          categoryInMemRepo <- CategoryRepository.inmemory[ConnectionIO]
          categoryRepo = CategoryRepository.withEffect[ConnectionIO](FunctionK.id)
          categoryFixtureRes <- new CategoryFixture[ConnectionIO](categoryRepo).init()
          categoryGenerators = new CategoryGenerators[ConnectionIO](categoryFixtureRes)
          catCheckers = new Assertions[CategoryCrud] {
            def update(c: UpdateCategory, v: Category): Any = {
              c.readableId.foreach(_ should equal(v.readableId))
              c.description.foreach(_ should equal(v.description))
              c.displayName.foreach(_ should equal(v.displayName))
            }

            def create(c: CreateCategory, v: Category): Any = {
              c.readableId should equal(v.readableId)
              c.displayName should equal(v.displayName)
              c.description should equal(v.description)
            }
          }
          imageRepo = ImageRepositoryImpl
          imageFixtureRes <- new ImageFixture[ConnectionIO](imageRepo).init()
          imageGenerators = new ImageGenerators[ConnectionIO](imageFixtureRes)
          imageCheckers = new Assertions[ImageCrud] {
            def update(c: BuzzImageUpdate, v: Image): Any = succeed

            def create(c: CreateImageMetadata, v: Image): Any = {
              c.src should equal(v.src)
              c.alt should equal(v.alt)
            }
          }
        } yield (
          (trans, EntityTest[ConnectionIO, CategoryCrud](categoryRepo, categoryGenerators, catCheckers, implicitly)),
          (
            trans,
            EntityTest[ConnectionIO, CategoryCrud](categoryInMemRepo, categoryGenerators, catCheckers, implicitly)
          ),
          (trans, EntityTest[ConnectionIO, ImageCrud](imageRepo, imageGenerators, imageCheckers, implicitly))
        )
      })
    } yield res
  }
}
