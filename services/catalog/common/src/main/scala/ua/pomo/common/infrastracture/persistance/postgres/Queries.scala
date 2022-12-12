package ua.pomo.common.infrastracture.persistance.postgres

import ua.pomo.common.domain.repository.{Query, Crud}
import doobie._

trait Queries[T <: Crud] {
  def create(req: T#Create): List[Update0]
  def delete(id: T#EntityId): List[Update0]
  def find(query: Query[T#Selector]): Query0[T#Entity]
  def update(req: T#Update): List[Update0]
}
