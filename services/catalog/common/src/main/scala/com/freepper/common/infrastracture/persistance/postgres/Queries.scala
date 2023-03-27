package com.freepper.common.infrastracture.persistance.postgres

import doobie.*
import com.freepper.common.domain.crud.{Crud, Query}

import Crud._

trait Queries[C[_]] {
  def create(req: C[Create]): List[Update0]
  def delete(id: C[EntityId]): List[Update0]
  def find(query: C[Crud.Query]): Query0[C[Entity]]
  def update(req: C[Crud.Update]): List[Update0]
}
