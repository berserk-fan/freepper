package com.freepper.common.infrastracture.persistance

import cats.arrow.FunctionK
import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, Repository}

import Crud.*

case class RepositoryK[F[_], G[_], C[_]] private (r: Repository[F, C], xa: FunctionK[F, G]) extends Repository[G, C] {
  override def create(createReq: C[Create]): G[C[EntityId]] = xa(r.create(createReq))

  override def get(id: C[EntityId]): G[C[Entity]] = xa(r.get(id))

  override def find(id: C[EntityId]): G[Option[C[Entity]]] = xa(r.find(id))

  override def findAll(req: C[Query]): G[List[C[Entity]]] = xa(r.findAll(req))

  override def update(req: C[Update]): G[Int] = xa(r.update(req))

  override def delete(id: C[EntityId]): G[Int] = xa(r.delete(id))
}
