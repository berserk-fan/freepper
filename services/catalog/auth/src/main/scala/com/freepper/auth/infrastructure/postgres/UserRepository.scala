package com.freepper.auth.infrastructure.postgres

import cats.{Functor, Inject, MonadThrow}
import com.freepper.auth.domain.user.Crud.{Create, Entity, EntityId, Selector, Update}
import com.freepper.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, DoobieInstances, Queries, QueryHelpers}
import com.freepper.auth.domain.user.*
import com.freepper.common.domain.crud
import .Repository
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepositoryV2, InMemFieldDef, InMemoryQueryHelpers}
import cats.effect.{Ref, Sync}
import cats.syntax.functor.toFunctorOps
import com.freepper.common.infrastracture.persistance.GenericSelector
import doobie.{ConnectionIO, Get, Meta}
import monocle.syntax.all.*
import doobie.implicits.toSqlInterpolator
import monocle.Getter
import shapeless.*

object UserRepository {

  import DoobieInstances.timeInstances.UtcInstantMeta
  import DoobieInstances.commonInstances.*
  import AuthReposFieldDefs.*
  def postgres: Repository[ConnectionIO, UserCrud] = UserRepository

  def inmemory[F[_]: Sync]: F[Repository[F, UserCrud]] =
    Ref.of[F, Map[UserCrud#EntityId, User]](Map[UserCrud#EntityId, User]()).map(new InMemoryUserRepository(_))

  object UserQueries extends Queries[UserCrud] {
    private val tableName: String = "users"

    override def create(req: Create): List[doobie.Update0] = {
      List(
        sql"""INSERT into users (id, display_name, email, image_src)
         VALUES (${req.id}, ${req.displayName}, ${req.email}, ${req.imageSrc})""".update
      )
    }

    override def delete(id: EntityId): List[doobie.Update0] = {
      QueryHelpers.defaultDelete(tableName, id)
    }

    override def update(req: Update): List[doobie.Update0] = {
      QueryHelpers.update(req, tableName)
    }

    override def find(query: crud.Query[Selector]): doobie.Query0[Entity] = {
      implicit val readUser: Get[User] = DoobieInstances.commonInstances.readJsonFromView[User]
      QueryHelpers.query(query, "user_views")
    }

  }

  private object UserRepository extends AbstractPostgresRepository[UserCrud](UserQueries) {
    override protected def findQuery: EntityId => Selector = id => {
      GenericSelector.FieldEquals[UserDomainField](Inject[UserId, UserDomainField](id))
    }
  }

  private class InMemoryUserRepository[F[_]: MonadThrow](ref: Ref[F, Map[UserCrud#EntityId, User]])
      extends AbstractInMemoryRepositoryV2[F, UserCrud](ref) {
    override protected def creator(c: Create): Entity = {
      User(UserUid(java.util.UUID.randomUUID()), c.id, c.displayName, c.email, None, c.imageSrc)
    }

    override protected def filter(s: Selector, e: Entity): Boolean = {
      implicit val qq: UserDomainField => Lens[User, UserDomainField] = (domainField) => {
        
      }

      InMemoryQueryHelpers.generateFilter(e, s)
    }

    override def update(req: Update): F[Int] = {
      implicit val ig: IgnoredInUpdate[UserSelector] = ignore[UserSelector]
      defaultUpdate(req)
    }
  }
}
