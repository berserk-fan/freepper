package com.freepper.common.app.programs

import cats.MonadThrow
import cats.data.Kleisli
import com.freepper.common.domain.auth.{CallContext, UserRole}
import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, Service}
import com.freepper.common.domain.error.NoPermission

case class SecuredService[F[_]: MonadThrow, T <: Crud](delegate: Service[F, T])
    extends Service[Kleisli[F, CallContext, *], T] {
  override def create(createCommand: T#Create): Kleisli[F, CallContext, T#Entity] = {
    forceAdmin(delegate.create(createCommand))
  }

  override def get(id: T#EntityId): Kleisli[F, CallContext, T#Entity] = {
    forceNothing(delegate.get(id))
  }

  override def findAll(req: crud.Query[T#Selector]): Kleisli[F, CallContext, crud.ListResponse[T#Entity]] = {
    forceNothing(delegate.findAll(req))
  }

  override def update(updateCommand: T#Update): Kleisli[F, CallContext, T#Entity] = {
    forceAdmin(delegate.update(updateCommand))
  }

  override def delete(id: T#EntityId): Kleisli[F, CallContext, Unit] = {
    forceAdmin(delegate.delete(id))
  }

  private def forceRole[U](role: UserRole, proceed: F[U]): Kleisli[F, CallContext, U] = {
    Kleisli[F, CallContext, U] { ctx =>
      ctx.user.fold(MonadThrow[F].raiseError[U](NoPermission())) { user =>
        if (user.role != role) {
          MonadThrow[F].raiseError[U](NoPermission())
        } else {
          proceed
        }
      }
    }
  }

  private def forceAdmin[U](proceed: F[U]): Kleisli[F, CallContext, U] = {
    forceRole(UserRole.Admin, proceed)
  }

  private def forceNothing[U](proceed: F[U]): Kleisli[F, CallContext, U] = {
    Kleisli[F, CallContext, U] { _ => proceed }
  }
}
