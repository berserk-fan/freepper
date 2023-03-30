package com.freepper.common.infrastracture.persistance.postgres

import doobie.{Update0, Query0}
import com.freepper.common.domain.crud.Crud.*

trait Queries[C[_]] {
  def create(req: C[Create]): List[Update0]
  def delete(id: C[EntityId]): List[Update0]
  def find(query: C[Query]): Query0[C[Entity]]
  def update(req: C[Update]): List[Update0]
}
