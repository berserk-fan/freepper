package com.freepper.auth.infrastructure.postgres

import cats.{Applicative, Monad, Traverse}
import com.freepper.auth.domain.{RegistryHelper, user}
import com.freepper.auth.domain.user.UserCrud
import com.freepper.common.domain.Fixture
import cats.syntax.functor.toFunctorOps
import com.freepper.common.domain.registry.Registry
import org.scalacheck.Gen
import org.typelevel.log4cats.LoggerFactory
import cats.syntax.flatMap.toFlatMapOps

object AuthFixtures {
  private def init[F[_]: Applicative, T <: Crud](repo: Repository[F, T], e: List[T#Create]): F[Unit] = {
    Traverse[List].traverse(e)(repo.create).as(())
  }

  def fixtureRegistry[F[_]: Monad: LoggerFactory](
      registry: Registry[Lambda[`T <: Crud` => Repository[F, T]]]
  ): F[Registry[Fixture]] =
    for {
      logger <- LoggerFactory[F].create
      _ <- logger.info("Started to execute fixture object creation")
      _ <- init[F, UserCrud](registry.apply[UserCrud], UserFixture.entities)
      _ <- logger.info("User migration executed")
      _ <- logger.info("Finished to execute fixture object creation")
    } yield RegistryHelper.createRegistry[Fixture](UserFixture)

  object UserFixture extends Fixture[UserCrud] {
    override def entities: List[user.CreateUserCommand] =
      Gen.listOfN(5, AuthGenerators.registry.apply[UserCrud].create).sample.get
  }
}
