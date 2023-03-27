package com.freepper.common.app.programs

import cats.MonadThrow
import cats.data.Kleisli
import com.freepper.common.domain.auth.{CallContext, UserRole}
import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, Service}
import com.freepper.common.domain.error.NoPermission

import Crud._

case class SecuredService[F[_]: MonadThrow, C[_]](delegate: Service[F, C])
    extends Service[Kleisli[F, CallContext, *], C] {
  override def create(createCommand: C[Create]): Kleisli[F, CallContext, C[Entity]] = {
    forceAdmin(delegate.create(createCommand))
  }

  override def get(id: C[EntityId]): Kleisli[F, CallContext, C[Entity]] = {
    forceNothing(delegate.get(id))
  }

  override def findAll(req: C[Query]): Kleisli[F, CallContext, crud.ListResponse[C[Entity]]] = {
    forceNothing(delegate.findAll(req))
  }

  override def update(updateCommand: C[Update]): Kleisli[F, CallContext, C[Entity]] = {
    forceAdmin(delegate.update(updateCommand))
  }

  override def delete(id: C[EntityId]): Kleisli[F, CallContext, Unit] = {
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
