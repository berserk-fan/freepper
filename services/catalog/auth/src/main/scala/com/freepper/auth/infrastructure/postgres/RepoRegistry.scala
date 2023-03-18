package com.freepper.auth.infrastructure.postgres

import cats.effect.Sync
import doobie.ConnectionIO
import com.freepper.common.domain.registry.Registry
import com.freepper.auth.domain.RegistryHelper
import com.freepper.common.domain.crud.{Crud, Repository}
import cats.syntax.functor.toFunctorOps

object RepoRegistry {
  def inmemory[F[_]: Sync]: F[Registry.Aux[Repository, F]] = {
    UserRepository.inmemory[F].map(RegistryHelper.createRegistry)
  }

  def postgres: Registry.Aux[Repository, ConnectionIO] = {
    RegistryHelper.createRegistry(UserRepository.postgres)
  }
}
