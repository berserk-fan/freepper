package ua.pomo.common.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype

object repository {
  case class Query[T](selector: T, page: PageToken.NonEmpty)
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

    implicit val ops: CrudOps[self.type]
  }

  trait CrudOps[T <: Crud] {
    def getIdUpdate(update: T#Update): T#EntityId
    def getIdEntity(entity: T#Entity): T#EntityId
    def entityDisplayName: EntityDisplayName
  }

  object CrudOps {
    def apply[T <: Crud: CrudOps]: CrudOps[T] = implicitly[CrudOps[T]]
  }

  trait Repository[F[_], T <: Crud] {
    def create(createReq: T#Create): F[T#EntityId]
    def get(id: T#EntityId): F[T#Entity]
    def find(id: T#EntityId): F[Option[T#Entity]]
    def findAll(req: Query[T#Selector]): F[List[T#Entity]]
    def update(req: T#Update): F[Int]
    def delete(id: T#EntityId): F[Int]
  }
}
