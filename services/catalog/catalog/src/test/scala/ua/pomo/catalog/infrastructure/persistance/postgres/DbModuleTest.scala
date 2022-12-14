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
import ua.pomo.catalog.domain.{imageList, parameter}
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.infrastructure.persistance.postgres.CatalogRepositoryAbstractTest.SuiteResource
import ua.pomo.catalog.infrastructure.persistance.postgres.DbGenerators.{
  CategoryGenerators,
  ImageGenerators,
  ImageListGenerators
}
import ua.pomo.catalog.shared.FixturesV2.{CategoryFixture, ImageFixture}
import ua.pomo.common.domain.{Assertions, EntityTest, Schema}
import ua.pomo.common.{AppConfigLoader, DBMigrations, TransactorHelpers}

object DbModuleTest extends Matchers {

  def categoryPostgres: SuiteResource[CategoryCrud] = allModules(("category-postgres")).map(_._1)
  def categoryInMemory: SuiteResource[CategoryCrud] = allModules("category-inmem").map(_._2)
  def imagePostgres: SuiteResource[ImageCrud] = allModules("image-postgres").map(_._3)
  def imageListPostgres: SuiteResource[ImageListCrud] = allModules("image-list-postgres").map(_._4)
  def imageListInMemory: SuiteResource[ImageListCrud] = allModules("image-list-inmem").map(_._5)
  def parameterListPostgres: SuiteResource[ParameterListCrud] = allModules("parameter-list-postgres").map(_._6)

  private def allModules(schemaName: String) = {
    for {
      _ <- Resource.make(Sync[IO].blocking(println("Instantiating dbModuleTest")))(_ =>
        Sync[IO].blocking(println("Shutting down dbModuleTest"))
      )
      config <- Resource.eval(AppConfigLoader.loadDefault[IO, AppConfig]("catalog")).map { cfg =>
        cfg.copy(jdbc = cfg.jdbc.copy(schema = s"$schemaName-test-schema"))
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
          categoryGenerators = CategoryGenerators()
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
          entityTest1 = (
            trans,
            EntityTest[ConnectionIO, CategoryCrud](categoryRepo, categoryGenerators, catCheckers, implicitly)
          )
          entityTest2 = (
            trans,
            EntityTest[ConnectionIO, CategoryCrud](categoryInMemRepo, categoryGenerators, catCheckers, implicitly)
          )
          imageRepo = ImageRepositoryImpl
          imageFixtureRes <- new ImageFixture(imageRepo).init()
          imageGenerators = ImageGenerators()
          imageCheckers = new Assertions[ImageCrud] {
            def update(c: BuzzImageUpdate, v: Image): Any = succeed

            def create(c: CreateImageMetadata, v: Image): Any = {
              c.src should equal(v.src)
              c.alt should equal(v.alt)
            }
          }
          entityTest3 = (
            trans,
            EntityTest[ConnectionIO, ImageCrud](imageRepo, imageGenerators, imageCheckers, implicitly)
          )
          imageListRepo = ImageListRepository.postgres
          imageListInMemRepo <- ImageListRepository.inmemory[ConnectionIO]
          imageListGenerators = ImageListGenerators[ConnectionIO](imageFixtureRes)
          imageListCheckers = new Assertions[ImageListCrud] {
            override def update(c: imageList.UpdateImageList, v: imageList.ImageList): Any = {
              c.displayName.foreach(_ should equal(v.displayName))
              c.images.foreach(_ should equal(v.images.map(_.id)))
            }

            override def create(c: imageList.CreateImageList, v: imageList.ImageList): Any = {
              c.displayName should equal(v.displayName)
              c.images should equal(v.images.map(_.id))
            }
          }
          entityTest4 = (
            trans,
            EntityTest[ConnectionIO, ImageListCrud](imageListRepo, imageListGenerators, imageListCheckers, implicitly)
          )
          entityTest5 = (
            trans,
            EntityTest[ConnectionIO, ImageListCrud](
              imageListInMemRepo,
              imageListGenerators,
              imageListCheckers,
              implicitly
            )
          )
          parameterListRepo = ParameterListRepository.postgres
          parameterListGenerators = DbGenerators.ParameterList(imageFixtureRes)
          plAssertions = new Assertions[ParameterListCrud] {
            override def update(c: parameter.UpdateParameterList, v: parameter.ParameterList): Any = {
              c.displayName.foreach(_ should equal(v.displayName))

              c.parameters.foreach { parametersUpdate =>
                parametersUpdate.map(_.id.get) should equal(v.parameters.map(_.id))

                parametersUpdate.foreach { paramUpdate =>
                  val updated = v.parameters.find(_.id == paramUpdate.id.get).get
                  paramUpdate.displayName should equal(updated.displayName)
                  paramUpdate.image should equal(updated.image.map(_.id))
                  paramUpdate.description should equal(updated.description)
                }
              }
            }

            override def create(c: parameter.CreateParameterList, v: parameter.ParameterList): Any = {
              c.displayName should equal(v.displayName)

              c.parameters.map(cp => (cp.displayName, cp.image, cp.description)) should equal(
                v.parameters.map(p => (p.displayName, p.image.map(_.id), p.description))
              )
            }
          }

          entityTest6 = (
            trans,
            EntityTest[ConnectionIO, ParameterListCrud](
              parameterListRepo,
              parameterListGenerators,
              plAssertions,
              implicitly
            )
          )
        } yield (entityTest1, entityTest2, entityTest3, entityTest4, entityTest5, entityTest6)
      })
    } yield res
  }
}
