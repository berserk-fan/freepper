package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie.{Fragment, Fragments}
import ua.pomo.catalog.domain.category._
import ua.pomo.common.domain.repository
import ua.pomo.common.infrastracture.persistance.postgres.Queries
import doobie.implicits._
import doobie.postgres.implicits._

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

  override def update(cat: UpdateCategory): doobie.Update0 = {
    val rId = cat.readableId.map(x => fr"readable_id = $x")
    val dName = cat.displayName.map(x => fr"display_name = $x")
    val desc = cat.description.map(x => fr"description = $x")

    val setFr = Fragments.setOpt(List(rId, dName, desc): _*)
    if (Fragment.empty == setFr) {
      throw new IllegalArgumentException("Empty update in updateCategory")
    }
    sql"""
           update categories cat
           $setFr
           where id=${cat.id}
         """.update
  }
}
