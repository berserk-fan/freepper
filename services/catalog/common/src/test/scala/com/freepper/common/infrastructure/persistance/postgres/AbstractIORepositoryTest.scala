package com.freepper.common.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.effect.{IO, Resource}
import com.freepper.common.TestIORuntime
import com.freepper.common.domain.EntityTest
import com.freepper.common.domain.crud.Crud
import org.typelevel.log4cats.LoggerFactory
import TestIORuntime.runtime
import com.freepper.common.domain.crud.Crud.{EntityId, Update}

abstract class AbstractIORepositoryTest[F[_]: MonadThrow: LoggerFactory, C[_]](r: Resource[IO, EntityTest[F, IO, C]])(
    implicit updateId: monocle.Getter[C[Update], C[EntityId]]
) extends AbstractRepositoryTest[F, IO, C]() {
  override def suiteResource: Resource[IO, SuiteResource] = r
}
