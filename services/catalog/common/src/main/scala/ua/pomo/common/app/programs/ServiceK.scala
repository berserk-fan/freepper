package ua.pomo.common.app.programs

import cats.~>
import ua.pomo.common.domain.crud
import ua.pomo.common.domain.crud.{Crud, Service}

case class ServiceK[F[_], G[_], T <: Crud] private (s: Service[F, T], xa: F ~> G) extends Service[G, T] {
  private def mapK[U](value: F[U]): G[U] = xa.apply(value)

  override def create(createCommand: T#Create): G[T#Entity] = mapK(s.create(createCommand))

  override def get(id: T#EntityId): G[T#Entity] = mapK(s.get(id))

  override def findAll(req: crud.Query[T#Selector]): G[crud.ListResponse[T#Entity]] = mapK(s.findAll(req))

  override def update(updateCommand: T#Update): G[T#Entity] = mapK(s.update(updateCommand))

  override def delete(id: T#EntityId): G[Unit] = mapK(s.delete(id))
}
