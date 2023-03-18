package com.freepper.common.infrastracture.persistance.postgres

import cats.Show
import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import doobie.{ConnectionIO, Update0}
import com.freepper.common.domain.crud._
import com.freepper.common.domain.error.{DbErr, NotFound}

abstract class AbstractPostgresRepository[T <: Crud: ValueOf](val queries: Queries[T])(implicit
    createToId: monocle.Getter[T#Create, T#EntityId],
    show: Show[T]
) extends Repository[ConnectionIO, T] {

  protected def idSelector: T#EntityId => T#Selector

  private def sequenceUpdate(l: List[Update0]): ConnectionIO[Int] = {
    l.foldLeft(Sync[ConnectionIO].pure(0))((u1, u2) => u1.flatMap(res => u2.run.map(res2 => res + res2)))
  }

  override def create(model: T#Create): ConnectionIO[T#EntityId] = {
    val id = createToId.get(model)
    sequenceUpdate(queries.create(model)).as(id)
  }

  override def get(id: T#EntityId): ConnectionIO[T#Entity] = {
    val entityDisplayName: String = Show[T].show(implicitly[ValueOf[T]].value)
    OptionT(find(id)).getOrElseF(NotFound(entityDisplayName, id).raiseError[ConnectionIO, T#Entity])
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
