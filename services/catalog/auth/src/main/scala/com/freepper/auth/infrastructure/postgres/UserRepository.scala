package com.freepper.auth.infrastructure.postgres

import cats.MonadThrow
import com.freepper.auth.domain.user.Crud.{Create, Entity, EntityId, Selector, Update}
import com.freepper.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, DbFieldDef, DbUpdaterPoly, DoobieCompilerImpl, DoobieInstances, Queries, QueryHelpers}
import com.freepper.auth.domain.user._
import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.Repository
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import shapeless._
import cats.effect.{Ref, Sync}
import cats.syntax.functor.toFunctorOps
import com.freepper.common.infrastracture.persistance.postgres.QueryHelpers.Test.GenericSelector
import doobie.{ConnectionIO, Get, Meta}
import monocle.syntax.all._
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.util.log.LogHandler

object UserRepository {

  import DoobieInstances.timeInstances.UtcInstantMeta
  import DoobieInstances.commonInstances._
  import AuthReposFieldDefs._
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
      QueryHelpers.deleteEither(tableName, id)
    }

    override def update(req: Update): List[doobie.Update0] = {
      QueryHelpers.update(Generic[UpdateUserCommand].to(req), tableName)
    }

    override def find(query: crud.Query[Selector]): doobie.Query0[Entity] = {
      implicit val readUser: Get[User] = DoobieInstances.commonInstances.readJsonFromView[User]
      val where = GenericSelector.Compiler.compile("t", query.selector)
      
      val doobieComp = DoobieCompilerImpl()

      QueryHelpers.queryForGenericSelector(query.page, where, "users_view", Some("display_name"))
    }

  }

  private object UserRepository extends AbstractPostgresRepository[UserCrud](UserQueries) {
    override protected def idSelector: EntityId => Selector = id => {
      id.fold(
        GenericSelector.FieldEquals(_, implicitly[DbFieldDef[UserId]]),
        GenericSelector.FieldEquals(_, implicitly[DbFieldDef[UserUid]])
      )
    }
  }

  private class InMemoryUserRepository[F[_]: MonadThrow](ref: Ref[F, Map[UserCrud#EntityId, User]])
      extends AbstractInMemoryRepository[F, UserCrud](ref) {
    override protected def creator: Create => Entity = c =>
      User(UserUid(java.util.UUID.randomUUID()), c.id, c.displayName, c.email, None, c.imageSrc)

    override protected def filter: Selector => Entity => Boolean = {
      case UserSelector.All        => _ => true
      case UserSelector.IdIs(id)   => c => c.id == id
      case UserSelector.UidIs(uid) => c => c.uid == uid
    }

    object updaterObj extends InMemoryUpdaterPoly[User] {
      implicit val uid: Res[UserUid] = gen(_.focus(_.uid))
      implicit val displayName: Res[UserDisplayName] = gen(_.focus(_.displayName))
      implicit val description: Res[UserEmail] = gen(_.focus(_.email))
      implicit val emailVerificationTime: Res[Option[UserEmailVerificationTime]] = gen(_.focus(_.emailVerificationTime))
      implicit val imageSrc: Res[Option[UserImageSrc]] = gen(_.focus(_.imageSrc))
    }

    override def update(req: Update): F[Int] = {
      updateHelper(req, updaterObj, Generic[UpdateUserCommand])
    }
  }
}
