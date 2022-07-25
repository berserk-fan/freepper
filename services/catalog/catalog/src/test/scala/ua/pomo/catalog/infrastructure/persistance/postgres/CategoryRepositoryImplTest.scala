package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import doobie.implicits._
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.CategoryCrud
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.FixturesV2.{AbstractFixture, CategoryFixture}
import ua.pomo.catalog.shared.{Generators, _}
import ua.pomo.common.{AppConfigLoader, DbResources, DbUnitTestSuite}
import ua.pomo.common.domain.repository.{PageToken, Query, Repository}
import ua.pomo.common.infrastructure.persistance.postgres.AbstractRepositoryTest
import monocle.macros.GenLens
import ua.pomo.catalog.AppConfig
import ua.pomo.common.UnsafeRunnable.UnsafeRunnableSyntax
import ua.pomo.common.infrastracture.persistance.RepositoryK

import java.util.UUID

class CategoryPostgresRepoImplTest
    extends CategoryRepositoryImplTest1(db =>
      Resource.eval(CategoryRepositoryImpl.makeInMemory[IO]()).map(RepositoryK(_, db.xa))
    )

class CategoryInMemoryRepoImplTest
    extends CategoryRepositoryImplTest1(db => Resource.pure(RepositoryK(CategoryRepositoryImpl.makeInMemory(), db.xa)))

abstract class CategoryRepositoryImplTest1(repo: DbResources => Resource[IO, Repository[IO, CategoryCrud.type]])
    extends AbstractRepositoryTest[ConnectionIO, CategoryCrud.type](
      repo,
      DbGenerators.CategoryGenerators,
      Resource.eval(IO.blocking(new AbstractFixture[IO]() with CategoryFixture)),
      Resource.eval(AppConfigLoader.loadDefault[IO, AppConfig]("category").map(_.jdbc)),
      List(
        (GenLens[UpdateCategory](_.readableId), GenLens[Category](_.readableId)),
        (GenLens[UpdateCategory](_.displayName), GenLens[Category](_.displayName)),
        (GenLens[UpdateCategory](_.description), GenLens[Category](_.description))
      ),
      List(
        (GenLens[CreateCategory](_.readableId), GenLens[Category](_.readableId)),
        (GenLens[CreateCategory](_.displayName), GenLens[Category](_.displayName)),
        (GenLens[CreateCategory](_.description), GenLens[Category](_.description))
      )
    ) {

  testR(s"description not empty") { ctx =>
    val req = CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c"))
    val res = ctx.repository.create(req).trRun()
    ctx.repository.get(res).trRun().description.value should equal("c")
  }
}
