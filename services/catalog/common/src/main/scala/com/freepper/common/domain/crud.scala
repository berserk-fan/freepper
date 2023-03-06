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

  trait RepoOps[T <: Crud] {
    def getIdUpdate(update: T#Update): T#EntityId
    def getIdCreate(create: T#Create): T#EntityId
    def getIdEntity(entity: T#Entity): T#EntityId
    def entityDisplayName: EntityDisplayName
  }

  object RepoOps {
    def apply[T <: Crud: RepoOps]: RepoOps[T] = implicitly[RepoOps[T]]
  }

  trait Repository[F[_], T <: Crud] {
    def create(createReq: T#Create): F[T#EntityId]
    def get(id: T#EntityId): F[T#Entity]
    def find(id: T#EntityId): F[Option[T#Entity]]
    def findAll(req: Query[T#Selector]): F[List[T#Entity]]
    def update(req: T#Update): F[Int]
    def delete(id: T#EntityId): F[Int]
  }

  object Repository {
    type Registry[F[_]] = registry.Registry[Lambda[`T <: Crud` => Repository[F, T]]]
  }

  trait ServiceOps[T <: Crud] {
    def getIdUpdate(update: T#Update): T#EntityId
    def entityDisplayName: EntityDisplayName

  }

  object ServiceOps {
    def apply[T <: Crud: ServiceOps]: ServiceOps[T] = implicitly[ServiceOps[T]]
    implicit def fromRepoOps[T <: Crud](implicit repoOps: RepoOps[T]): ServiceOps[T] = new ServiceOps[T] {
      override def getIdUpdate(update: T#Update): T#EntityId = repoOps.getIdUpdate(update)

      override def entityDisplayName: EntityDisplayName = repoOps.entityDisplayName
    }
  }

  trait Service[F[_], T <: Crud] {
    def create(createCommand: T#Create): F[T#Entity]

    def get(id: T#EntityId): F[T#Entity]

    def findAll(req: Query[T#Selector]): F[ListResponse[T#Entity]]

    def update(updateCommand: T#Update): F[T#Entity]

    def delete(id: T#EntityId): F[Unit]
  }

}
