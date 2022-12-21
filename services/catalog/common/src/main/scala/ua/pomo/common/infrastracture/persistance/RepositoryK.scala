package ua.pomo.common.infrastracture.persistance

import cats.arrow.FunctionK
import ua.pomo.common.domain.crud
import ua.pomo.common.domain.crud.{Crud, Repository}

case class RepositoryK[F[_], G[_], T <: Crud] private (r: Repository[F, T], xa: FunctionK[F, G])
    extends Repository[G, T] {
  override def create(createReq: T#Create): G[T#EntityId] = xa(r.create(createReq))

  override def get(id: T#EntityId): G[T#Entity] = xa(r.get(id))

  override def find(id: T#EntityId): G[Option[T#Entity]] = xa(r.find(id))

  override def findAll(req: crud.Query[T#Selector]): G[List[T#Entity]] = xa(r.findAll(req))

  override def update(req: T#Update): G[Int] = xa(r.update(req))

  override def delete(id: T#EntityId): G[Int] = xa(r.delete(id))
}
