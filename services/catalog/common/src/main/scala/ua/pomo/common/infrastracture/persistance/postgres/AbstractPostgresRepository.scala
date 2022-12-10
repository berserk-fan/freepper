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

abstract class AbstractPostgresRepository[T <: Crud: CrudOps](val queries: Queries[T])
    extends Repository[ConnectionIO, T] {
  protected def idSelector: T#EntityId => T#Selector

  override def create(model: T#Create): ConnectionIO[T#EntityId] = {
    val (query, id) = queries.create(model)
    query.run.as(id)
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
    queries.delete(id).run
  }

  override def update(req: T#Update): ConnectionIO[Int] = {
    queries.update(req).fold(Sync[ConnectionIO].pure(0))(_.run)
  }
}
