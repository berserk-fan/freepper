package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import cats.~>
import doobie.ConnectionIO
import ua.pomo.common.TestIORuntime
import ua.pomo.common.domain.EntityTest
import ua.pomo.common.domain.repository.{Crud, CrudOps}
import ua.pomo.common.infrastructure.persistance.postgres.RepositoryAbstractTest
import TestIORuntime.runtime
import org.typelevel.log4cats.slf4j.loggerFactoryforSync

abstract class CatalogRepositoryAbstractTest[T <: Crud: CrudOps](r: CatalogRepositoryAbstractTest.SuiteResource[T])
    extends RepositoryAbstractTest[ConnectionIO, IO, T]() {
  override def suiteResource: CatalogRepositoryAbstractTest.SuiteResource[T] = r
}

object CatalogRepositoryAbstractTest {
  type SuiteResource[T <: Crud] = Resource[IO, (ConnectionIO ~> IO, EntityTest[ConnectionIO, T])]
}
