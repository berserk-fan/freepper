package com.freepper.auth.domain

import com.freepper.auth.domain.user.UserCrud
import com.freepper.common.domain.registry.Registry
import com.freepper.common.domain.crud.Crud

object RegistryHelper {
  object implicits {
    implicit class RegistryOps[F[_ <: Crud]](r: Registry[F]) {
      def user: F[UserCrud] = r.apply[UserCrud]
    }
  }

  def createRegistry[F[_ <: Crud]](f1: F[UserCrud]): Registry[F] = {
    new Registry[F] {
      override def apply[T <: Crud: ValueOf]: F[T] = implicitly[ValueOf[T]].value match {
        case user.Crud => f1.asInstanceOf[F[T]]
        case _         => throw new IllegalArgumentException()
      }
    }
  }

  def usingImplicits[F[_ <: Crud]](implicit f1: F[UserCrud]): Registry[F] = { createRegistry(f1) }

}
