package ua.pomo.common.domain.registry

import ua.pomo.common.domain.crud.Crud

trait RegistryMapper[F[_], G[_]] {
  def apply[T <: Crud](f: F[T]): G[T]
}

trait RegistryMapper2[F1[_ <: Crud], F2[_ <: Crud], G[_ <: Crud]] {
  def apply[T <: Crud](f: F1[T], f2: F2[T]): G[T]
}

trait RegistryMapper3[F1[_], F2[_], F3[_], G[_]] {
  def apply[T <: Crud](f: F1[T], f2: F2[T], f3: F3[T]): G[T]
}

trait RegistryMapper4[F1[_], F2[_], F3[_], F4[_], G[_]] {
  def apply[T <: Crud](f: F1[T], f2: F2[T], f3: F3[T], f4: F4[T]): G[T]
}

trait RegistryMapper5[F1[_ <: Crud], F2[_ <: Crud], F3[_ <: Crud], F4[_ <: Crud], F5[_ <: Crud], G[_ <: Crud]] {
  def apply[T <: Crud](f: F1[T], f2: F2[T], f3: F3[T], f4: F4[T], f5: F5[T]): G[T]
}
