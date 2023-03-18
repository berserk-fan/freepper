package com.freepper.common.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype

object crud {
  case class Query[T](selector: T, page: PageToken.NonEmpty)
  case class ListResponse[T](entities: List[T], nextPageToken: PageToken)

  @derive(eqv, show)
  sealed trait PageToken
  object PageToken {
    @derive(eqv, show)
    case object Empty extends PageToken
    @derive(eqv, show, encoder, decoder)
    case class NonEmpty(size: Long, offset: Long) extends PageToken

    val One: NonEmpty = NonEmpty(1, 0)
    val Two: NonEmpty = NonEmpty(2, 0)
  }

  @derive(eqv, show)
  @newtype
  case class EntityDisplayName(value: String)

  trait Crud { self: Singleton =>
    type Create
    type Update
    type Entity
    type EntityId
    type Selector
  }

  trait Repository[F[_], T <: Crud] {
    def create(createReq: T#Create): F[T#EntityId]
    def get(id: T#EntityId): F[T#Entity]
    def find(id: T#EntityId): F[Option[T#Entity]]
    def findAll(req: Query[T#Selector]): F[List[T#Entity]]
    def update(req: T#Update): F[Int]
    def delete(id: T#EntityId): F[Int]
  }

  trait Service[F[_], T <: Crud] {
    def create(createCommand: T#Create): F[T#Entity]

    def get(id: T#EntityId): F[T#Entity]

    def findAll(req: Query[T#Selector]): F[ListResponse[T#Entity]]

    def update(updateCommand: T#Update): F[T#Entity]

    def delete(id: T#EntityId): F[Unit]
  }

}
