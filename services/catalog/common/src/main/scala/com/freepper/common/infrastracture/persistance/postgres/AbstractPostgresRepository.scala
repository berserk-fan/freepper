package com.freepper.common.infrastracture.persistance.postgres

import cats.Show
import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import com.freepper.common.domain.TypeName
import doobie.{ConnectionIO, Update0}
import com.freepper.common.domain.crud.*
import com.freepper.common.domain.error.NotFound

import Crud._

abstract class AbstractPostgresRepository[C[_]](val queries: Queries[C])(implicit
    createToId: monocle.Getter[C[Create], C[EntityId]],
    show: TypeName[C]
) extends Repository[ConnectionIO, C] {

  protected def findQuery: C[EntityId] => C[Crud.Query]

  private def sequenceUpdate(l: List[Update0]): ConnectionIO[Int] = {
    l.foldLeft(Sync[ConnectionIO].pure(0))((u1, u2) => u1.flatMap(res => u2.run.map(res2 => res + res2)))
  }

  override def create(model: C[Create]): ConnectionIO[C[EntityId]] = {
    val id = createToId.get(model)
    sequenceUpdate(queries.create(model)).as(id)
  }

  override def get(id: C[EntityId]): ConnectionIO[C[Entity]] = {
    val entityDisplayName: String = show.name
    OptionT(find(id)).getOrElseF(NotFound(entityDisplayName, id).raiseError[ConnectionIO, C[Entity]])
  }

  override def find(id: C[EntityId]): ConnectionIO[Option[C[Entity]]] = {
    OptionT(queries.find(findQuery(id)).option).value
  }

  override def findAll(req: C[Crud.Query]): ConnectionIO[List[C[Entity]]] = {
    queries.find(req).to[List]
  }

  override def delete(id: C[EntityId]): ConnectionIO[Int] = {
    sequenceUpdate(queries.delete(id))
  }

  override def update(req: C[Update]): ConnectionIO[Int] = {
    sequenceUpdate(queries.update(req))
  }
}
