package com.freepper.common.app.programs

import cats.~>
import com.freepper.common.domain.crud.{Crud, Service}
import com.freepper.common.domain.crud

import Crud.*

case class ServiceK[F[_], G[_], C[_]](s: Service[F, C], xa: F ~> G) extends Service[G, C] {
  private def mapK[U](value: F[U]): G[U] = xa.apply(value)

  override def create(createCommand: C[Create]): G[C[Entity]] = mapK(s.create(createCommand))

  override def get(id: C[EntityId]): G[C[Entity]] = mapK(s.get(id))

  override def findAll(req: C[Query]): G[crud.ListResponse[C[Entity]]] = mapK(s.findAll(req))

  override def update(updateCommand: C[Update]): G[C[Entity]] = mapK(s.update(updateCommand))

  override def delete(id: C[EntityId]): G[Unit] = mapK(s.delete(id))
}
