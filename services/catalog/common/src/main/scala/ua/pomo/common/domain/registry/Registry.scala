package ua.pomo.common.domain.registry

import ua.pomo.common.domain.repository.{Crud, CrudOps}

trait Registry[F[_ <: Crud]] {
  def apply[T <: Crud: ValueOf]: F[T]
}

object Registry {
  type Const[T] = Registry[Lambda[U => T]]

  def const[U](t: U): Registry[Lambda[`X <: Crud` => U]] = new Registry[Lambda[`X <: Crud` => U]] {
    override def apply[T <: Crud: ValueOf]: U = t
  }

  def map1[F1[_], G[_]](r1: Registry[F1])(f: RegistryMapper[F1, G]): Registry[G] = {
    new Registry[G] {
      override def apply[T <: Crud: ValueOf]: G[T] = {
        val a1 = r1.apply[T]
        f.apply(a1)
      }
    }
  }

  def map2[F1[_], F2[_], G[_]](r1: Registry[F1], r2: Registry[F2])(
      f: RegistryMapper2[F1, F2, G]
  ): Registry[G] = {
    new Registry[G] {
      override def apply[T <: Crud: ValueOf]: G[T] = {
        val a1 = r1.apply[T]
        val a2 = r2.apply[T]
        f.apply(a1, a2)
      }
    }
  }

  def map3[F1[_], F2[_], F3[_], G[_]](r1: Registry[F1], r2: Registry[F2], r3: Registry[F3])(
      f: RegistryMapper3[F1, F2, F3, G]
  ): Registry[G] = {
    new Registry[G] {
      override def apply[T <: Crud: ValueOf]: G[T] = {
        val a1 = r1.apply[T]
        val a2 = r2.apply[T]
        val a3 = r3.apply[T]
        f.apply(a1, a2, a3)
      }
    }
  }

  def map4[F1[_], F2[_], F3[_], F4[_], G[_]](
      r1: Registry[F1],
      r2: Registry[F2],
      r3: Registry[F3],
      r4: Registry[F4]
  )(
      f: RegistryMapper4[F1, F2, F3, F4, G]
  ): Registry[G] = {
    new Registry[G] {
      override def apply[T <: Crud: ValueOf]: G[T] = {
        val a1 = r1.apply[T]
        val a2 = r2.apply[T]
        val a3 = r3.apply[T]
        val a4 = r4.apply[T]
        f.apply(a1, a2, a3, a4)
      }
    }
  }

  def map5[F1[_ <: Crud], F2[_ <: Crud], F3[_ <: Crud], F4[_ <: Crud], F5[_ <: Crud], G[_ <: Crud]](
      r1: Registry[F1],
      r2: Registry[F2],
      r3: Registry[F3],
      r4: Registry[F4],
      r5: Registry[F5]
  )(
      f: RegistryMapper5[F1, F2, F3, F4, F5, G]
  ): Registry[G] = {
    new Registry[G] {
      override def apply[T <: Crud: ValueOf]: G[T] = {
        val a1 = r1.apply[T]
        val a2 = r2.apply[T]
        val a3 = r3.apply[T]
        val a4 = r4.apply[T]
        val a5 = r5.apply[T]
        f.apply(a1, a2, a3, a4, a5)
      }
    }
  }

}
