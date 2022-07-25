package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.syntax.functor._
import cats.~>
import doobie.ConnectionIO
import ua.pomo.catalog.domain.category._
import ua.pomo.common.infrastracture.persistance.RepositoryK
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

case class CategoryRepositoryImpl private () extends AbstractPostgresRepository[CategoryCrud](CategoryQueries) {
  override protected def idSelector: CategoryUUID => CategorySelector = CategorySelector.UidIs.apply
}

object CategoryRepositoryImpl {
  def makeInMemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(Map[CategoryUUID, Category]()).map(CategoryInMemoryRepositoryImpl(_))
  }
  def withEffect[F[_]](transactor: ConnectionIO ~> F): CategoryRepository[F] = {
    RepositoryK[ConnectionIO, F, CategoryCrud](new CategoryRepositoryImpl(), transactor)
  }
}
