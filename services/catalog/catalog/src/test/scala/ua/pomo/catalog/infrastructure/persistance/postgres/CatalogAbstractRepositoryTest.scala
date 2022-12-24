package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.effect.{IO, Resource}
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.common.TestIORuntime
import ua.pomo.common.domain.EntityTest
import ua.pomo.common.domain.crud.Crud
import ua.pomo.common.infrastructure.persistance.postgres.AbstractRepositoryTest

import TestIORuntime.runtime

abstract class CatalogAbstractRepositoryTest[F[_]: MonadThrow: LoggerFactory, T <: Crud](
    r: Resource[IO, EntityTest[F, IO, T]]
) extends AbstractRepositoryTest[F, IO, T]() {
  override def suiteResource: Resource[IO, SuiteResource] = r
}
