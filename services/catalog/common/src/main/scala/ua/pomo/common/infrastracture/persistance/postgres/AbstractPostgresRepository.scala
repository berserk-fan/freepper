package ua.pomo.common.infrastracture.persistance.postgres

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Fragment, Fragments, Update0}
import shapeless.{Generic, HList, Nat}
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import ua.pomo.common.domain.error.NotFound
import ua.pomo.common.domain.repository._
import ua.pomo.common.domain.error.DbErr

abstract class AbstractPostgresRepository[T <: Crud: CrudOps](val queries: Queries[T])
    extends Repository[ConnectionIO, T] {
  protected def idSelector: T#EntityId => T#Selector

  private def sequenceUpdate(l: List[Update0]): ConnectionIO[Int] = {
    l.foldLeft(Sync[ConnectionIO].pure(0))((u1, u2) => u1.flatMap(res => u2.run.map(res2 => res + res2)))
  }

  override def create(model: T#Create): ConnectionIO[T#EntityId] = {
    CrudOps[T]
      .getIdCreate(model)
      .fold(Sync[ConnectionIO].raiseError[T#EntityId](DbErr(s"Can't create entity. No id provided. command: $model"))) {
        id =>
          sequenceUpdate(queries.create(model)).as(id)
      }
  }

  override def get(id: T#EntityId): ConnectionIO[T#Entity] = {
    OptionT(find(id)).getOrElseF(NotFound(CrudOps[T].entityDisplayName.value, id).raiseError[ConnectionIO, T#Entity])
  }

  override def find(id: T#EntityId): ConnectionIO[Option[T#Entity]] = {
    OptionT(queries.find(Query(idSelector(id), PageToken.NonEmpty(2, 0))).option).value
  }

  override def findAll(req: Query[T#Selector]): ConnectionIO[List[T#Entity]] = {
    queries.find(req).to[List]
  }

  override def delete(id: T#EntityId): ConnectionIO[Int] = {
    sequenceUpdate(queries.delete(id))
  }

  override def update(req: T#Update): ConnectionIO[Int] = {
    sequenceUpdate(queries.update(req))
  }
}
