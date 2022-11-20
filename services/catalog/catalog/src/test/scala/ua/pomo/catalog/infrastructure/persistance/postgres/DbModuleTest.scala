package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource, Sync}
import cats.~>
import cats.arrow.FunctionK
import ua.pomo.catalog.AppConfig
import doobie.{ConnectionIO, Fragment, Transactor}
import doobie.util.testing.UnsafeRun
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import doobie.implicits.toSqlInterpolator
import ua.pomo.common.{AppConfigLoader, DBMigrations, HasIORuntime, TransactorHelpers}
import ua.pomo.common.config.JdbcDatabaseConfig
import ua.pomo.common.domain.{
  Checkers,
  DbModule,
  DbTestModule,
  EntityTest,
  Generators,
  RepoTestRegistry,
  RepositoryName,
  Schema,
  repository
}
import ua.pomo.common.infrastructure.persistance.postgres.AbstractDbModuleTest
import pureconfig.generic.auto._
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.infrastructure.persistance.postgres.DbGenerators.{CategoryGenerators, ImageGenerators}
import ua.pomo.catalog.shared.FixturesV2.{CategoryFixture, ImageFixture}
import ua.pomo.common.domain.repository.{CrudOps, Repository}
import cats.syntax.functor.toFunctorOps
import ua.pomo.catalog.domain.image.{BuzzImageUpdate, CreateImageMetadata, Image, ImageCrud}

object DbModuleTest extends Matchers {
  lazy val transactor: Transactor[IO] = {
    println("Instantiating transactor")

    AppConfigLoader
      .loadDefault[IO, AppConfig]("catalog")
      .map { config =>
        TransactorHelpers.fromConfig[IO](config.jdbc)
      }
      .unsafeRunSync()(HasIORuntime.runtime)
  }

  lazy val repoNames: Seq[RepositoryName] = List(
    RepositoryName("category postgres"),
    RepositoryName("category inmemory"),
    RepositoryName("image postgres")
  )

  def makeLogger[F[_]: Sync]: Logger[F] = {
    new Logger[F] {
      override def error(t: Throwable)(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def warn(t: Throwable)(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def info(t: Throwable)(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def debug(t: Throwable)(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def trace(t: Throwable)(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def error(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def warn(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def info(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def debug(message: => String): F[Unit] = Sync[F].blocking(println(message))
      override def trace(message: => String): F[Unit] = Sync[F].blocking(println(message))
    }

  }

  lazy val resource: Resource[ConnectionIO, DbTestModule[ConnectionIO]] = {
    for {
      _ <- Resource.make(Sync[ConnectionIO].blocking(println("Instantiating dbModuleTest")))(_ =>
        Sync[ConnectionIO].blocking(println("Shutting down dbModuleTest"))
      )
      config <- Resource.eval(AppConfigLoader.loadDefault[ConnectionIO, AppConfig]("catalog"))
      l = makeLogger[ConnectionIO]
      schema1 <- Resource.make(DBMigrations.migrate[ConnectionIO](config.jdbc)(implicitly, l).as(Schema())) { _ =>
        sql"""DROP SCHEMA IF EXISTS "${Fragment.const0(config.jdbc.schema)}" CASCADE;""".update.run.as(())
      }
      categoryRepo = CategoryRepositoryImpl.withEffect[ConnectionIO](FunctionK.id)
      categoryInMemRepo <- Resource.eval(CategoryRepositoryImpl.makeInMemory[ConnectionIO])
      categoryFixtureRes <- Resource.eval(new CategoryFixture[ConnectionIO](categoryRepo).init())
      catCheckers = new Checkers[CategoryCrud] {
        def checkersUpdate(c: UpdateCategory, v: Category): Any = {
          c.readableId.foreach(_ should equal(v.readableId))
          c.description.foreach(_ should equal(v.description))
          c.displayName.foreach(_ should equal(v.displayName))
        }

        def checkersCreate(c: CreateCategory, v: Category): Any = {
          c.readableId should equal(v.readableId)
          c.displayName should equal(v.displayName)
          c.description should equal(v.description)
        }
      }
      imageRepo = ImageRepositoryImpl
      imageFixtureRes <- Resource.eval(new ImageFixture[ConnectionIO](imageRepo).init())
      imageCheckers = new Checkers[ImageCrud] {
        def checkersUpdate(c: BuzzImageUpdate, v: Image): Any = succeed
        def checkersCreate(c: CreateImageMetadata, v: Image): Any = {
          c.src should equal(v.src)
          c.alt should equal(v.alt)
        }
      }
    } yield {
      new DbTestModule[ConnectionIO] {
        def dbModule: DbModule[ConnectionIO] = new DbModule[ConnectionIO] {
          override def schema: Schema = schema1
          override def jdbcConfig: JdbcDatabaseConfig = config.jdbc
          override def transactor: ConnectionIO ~> ConnectionIO = FunctionK.id[ConnectionIO]
        }

        def dbTests: RepoTestRegistry[ConnectionIO] = new RepoTestRegistry[ConnectionIO] {
          override def value: List[EntityTest[ConnectionIO, _ <: repository.Crud]] = List(
            new EntityTest[ConnectionIO, CategoryCrud] {
              def checkers: Checkers[CategoryCrud] = catCheckers
              def co: CrudOps[CategoryCrud] = implicitly
              def generators: Generators[CategoryCrud] = new CategoryGenerators[ConnectionIO](categoryFixtureRes)
              def repository: Repository[ConnectionIO, CategoryCrud] = categoryRepo
              def repositoryName: RepositoryName = repoNames.head
            },
            new EntityTest[ConnectionIO, CategoryCrud] {
              def checkers: Checkers[CategoryCrud] = catCheckers
              def co: CrudOps[CategoryCrud] = implicitly
              def generators: Generators[CategoryCrud] = new CategoryGenerators[ConnectionIO](categoryFixtureRes)
              def repository: Repository[ConnectionIO, CategoryCrud] = categoryInMemRepo
              def repositoryName: RepositoryName = repoNames(1)
            },
            new EntityTest[ConnectionIO, ImageCrud] {
              def checkers: Checkers[ImageCrud] = imageCheckers
              def co: CrudOps[ImageCrud] = implicitly
              def generators: Generators[ImageCrud] = new ImageGenerators[ConnectionIO](imageFixtureRes)
              def repository: Repository[ConnectionIO, ImageCrud] = imageRepo
              def repositoryName: RepositoryName = repoNames(2)
            }
          )
        }
      }
    }
  }
}

class DbModuleTest
    extends AbstractDbModuleTest[ConnectionIO](
      DbModuleTest.resource,
      DbModuleTest.repoNames
    ) {

  override lazy val unsafeRun: doobie.util.testing.UnsafeRun[doobie.ConnectionIO] = new UnsafeRun[ConnectionIO] {
    override def unsafeRunSync[A](fa: ConnectionIO[A]): A =
      DbModuleTest.transactor.trans.apply(fa).unsafeRunSync()(HasIORuntime.runtime)
  }

  override lazy val monadCancelThrow: cats.effect.MonadCancelThrow[doobie.ConnectionIO] = implicitly
}
