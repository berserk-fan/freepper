package com.freepper.catalog.infrastructure.persistance.postgres

import cats.effect.{MonadCancelThrow, Ref, Sync}
import cats.syntax.functor._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import monocle.syntax.all._
import shapeless.Generic
import com.freepper.catalog.domain.category.{CategoryCrud, CategoryDescription, CategoryId, CategorySelector, _}
import com.freepper.common.domain.crud
import com.freepper.common.domain.error.DbErr
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import com.freepper.common.infrastracture.persistance.postgres.{
  AbstractPostgresRepository,
  DbUpdaterPoly,
  Queries,
  QueryHelpers
}

import java.util.UUID

object CategoryRepository {
  def inmemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(Map[CategoryId, Category]()).map(CategoryInMemoryRepositoryImpl(_))
  }

  def postgres: CategoryRepository[ConnectionIO] = {
    CategoryPostgresRepository()
  }

  object CategoryQueries extends Queries[CategoryCrud] {
    override def create(cat: CreateCategory): List[doobie.Update0] = List({
      val id = cat.id

      sql"""
           insert into categories (id, readable_id, display_name, description)
           VALUES ($id, ${cat.readableId}, ${cat.displayName}, ${cat.description})
         """.update

    })

    override def delete(id: CategoryId): List[doobie.Update0] = List({
      sql"""delete from categories cat
              where id=$id""".update
    })

    override def find(req: crud.Query[CategorySelector]): doobie.Query0[Category] = {
      val where = req.selector match {
        case CategorySelector.RidIs(rid) => fr"readable_id = $rid"
        case CategorySelector.UidIs(uid) => fr"id = $uid"
        case CategorySelector.All        => fr"1 = 1"
      }
      sql"""
          select cat.id, cat.readable_id, cat.display_name, cat.description
          from categories cat
          where $where
          order by cat.display_name
          ${compileToken(req.page)}
          """
        .query[Category]
    }

    object updaterObj extends DbUpdaterPoly {
      implicit val a1: Res[CategoryReadableId] = gen("readable_id")
      implicit val a2: Res[CategoryDisplayName] = gen("display_name")
      implicit val a3: Res[CategoryDescription] = gen("description")
    }

    override def update(cat: UpdateCategory): List[doobie.Update0] = {
      QueryHelpers
        .defaultUpdateRaw(Generic[UpdateCategory].to(cat), cat.id, updaterObj, "categories")
        .map(_.update)
        .toList
    }
  }

  private case class CategoryInMemoryRepositoryImpl[F[_]: MonadCancelThrow](ref: Ref[F, Map[CategoryId, Category]])
      extends AbstractInMemoryRepository[F, CategoryCrud](ref) {
    override protected def creator: CreateCategory => Category = (category: CreateCategory) => {
      val catUUID = CategoryId(UUID.randomUUID())
      Category(
        catUUID,
        category.readableId,
        category.displayName,
        category.description
      )
    }

    override protected def filter: CategorySelector => Category => Boolean = {
      case CategorySelector.RidIs(rid) => (x: Category) => x.readableId == rid
      case CategorySelector.UidIs(uid) => (x: Category) => x.id == uid
      case CategorySelector.All        => (_: Category) => true
    }

    private object updaterObj extends InMemoryUpdaterPoly[Category] {
      implicit val readableId: Res[CategoryReadableId] = gen(_.focus(_.readableId))
      implicit val displayName: Res[CategoryDisplayName] = gen(_.focus(_.displayName))
      implicit val description: Res[CategoryDescription] = gen(_.focus(_.description))
    }

    override def update(req: UpdateCategory): F[Int] = {
      updateHelper(req, updaterObj, Generic[UpdateCategory])
    }
  }

  private case class CategoryPostgresRepository() extends AbstractPostgresRepository[CategoryCrud](CategoryQueries) {
    override protected def idSelector: CategoryId => CategorySelector = CategorySelector.UidIs.apply
  }

}
