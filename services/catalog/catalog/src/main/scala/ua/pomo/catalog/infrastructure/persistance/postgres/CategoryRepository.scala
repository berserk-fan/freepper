package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.syntax.functor._
import cats.~>
import doobie.ConnectionIO
import ua.pomo.catalog.domain.category._
import ua.pomo.common.infrastracture.persistance.RepositoryK

object CategoryRepository {
  def inmemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(Map[CategoryUUID, Category]()).map(CategoryInMemoryRepositoryImpl(_))
  }

  def postgres: CategoryRepository[ConnectionIO] = {
    CategoryPostgresRepository()
  }
  def withEffect[F[_]](transactor: ConnectionIO ~> F): CategoryRepository[F] = {
    RepositoryK[ConnectionIO, F, CategoryCrud](CategoryPostgresRepository(), transactor)
  }
}
