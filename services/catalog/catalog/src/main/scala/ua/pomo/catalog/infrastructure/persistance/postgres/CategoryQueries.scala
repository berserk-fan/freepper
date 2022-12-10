package ua.pomo.catalog.infrastructure.persistance.postgres

import ua.pomo.catalog.domain.category._
import ua.pomo.common.domain.repository
import ua.pomo.common.infrastracture.persistance.postgres.{DbUpdaterPoly, Queries, QueriesHelpers}
import doobie.implicits._
import doobie.postgres.implicits._
import shapeless.Generic

import java.util.UUID

object CategoryQueries extends Queries[CategoryCrud] {
  override def create(cat: CreateCategory): (doobie.Update0, CategoryUUID) = {
    val id = CategoryUUID(UUID.randomUUID())

    val sql = sql"""
         insert into categories (id, readable_id, display_name, description)
         VALUES ($id, ${cat.readableId}, ${cat.displayName}, ${cat.description})
       """.update

    (sql, id)
  }

  override def delete(id: CategoryUUID): doobie.Update0 = {
    sql"""delete from categories cat
            where id=$id""".update
  }

  override def find(req: repository.Query[CategorySelector]): doobie.Query0[Category] = {
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

  override def update(cat: UpdateCategory): Option[doobie.Update0] = {
    QueriesHelpers[CategoryCrud]().updateQHelper(cat, updaterObj, "categories", Generic[UpdateCategory])
  }
}
