package com.freepper.common.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.effect.{IO, Resource}
import com.freepper.common.TestIORuntime
import com.freepper.common.domain.EntityTest
import com.freepper.common.domain.crud.Crud
import org.typelevel.log4cats.LoggerFactory

import TestIORuntime.runtime

abstract class AbstractIORepositoryTest[F[_]: MonadThrow: LoggerFactory, T <: Crud](
    r: Resource[IO, EntityTest[F, IO, T]]
) extends AbstractRepositoryTest[F, IO, T]() {
  override def suiteResource: Resource[IO, SuiteResource] = r
}
