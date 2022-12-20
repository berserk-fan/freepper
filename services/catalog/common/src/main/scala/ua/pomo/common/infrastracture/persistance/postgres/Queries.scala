package ua.pomo.common.infrastracture.persistance.postgres

import doobie._
import ua.pomo.common.domain.repository.{Crud, Query}

trait Queries[T <: Crud] {
  def create(req: T#Create): List[Update0]
  def delete(id: T#EntityId): List[Update0]
  def find(query: Query[T#Selector]): Query0[T#Entity]
  def update(req: T#Update): List[Update0]
}
