package com.freepper.common.domain.crud

import io.circe.{Decoder, Encoder, Json}
case class Query[T](selector: T, page: PageToken.NonEmpty)

case class ListResponse[T](entities: List[T], nextPageToken: PageToken)

sealed trait PageToken derives Decoder

object PageToken {

  case object Empty extends PageToken derives Decoder

  case class NonEmpty(size: Long, offset: Long) extends PageToken derives Decoder

  val One: NonEmpty = NonEmpty(1, 0)
  val Two: NonEmpty = NonEmpty(2, 0)
}

case class EntityDisplayName(value: String)

object Crud {
  object Create

  type Create = Create.type

  object Update

  type Update = Update.type

  object Entity

  type Entity = Entity.type

  object EntityId

  type EntityId = EntityId.type

  object Query

  type Query = Query.type
  
  object CrudName
  
  type CrudName = CrudName.type
}

import Crud.*
import io.circe.Decoder

trait Repository[F[_], C[_]] {
  def create(createReq: C[Create]): F[C[EntityId]]

  def get(id: C[EntityId]): F[C[Entity]]

  def find(id: C[EntityId]): F[Option[C[Entity]]]

  def findAll(req: C[Crud.Query]): F[List[C[Entity]]]

  def update(req: C[Update]): F[Int]

  def delete(id: C[EntityId]): F[Int]
}

trait Service[F[_], C[_]] {
  def create(createCommand: C[Create]): F[C[Entity]]

  def get(id: C[EntityId]): F[C[Entity]]

  def findAll(req: C[Crud.Query]): F[ListResponse[C[Entity]]]

  def update(updateCommand: C[Update]): F[C[Entity]]

  def delete(id: C[EntityId]): F[Unit]
}
