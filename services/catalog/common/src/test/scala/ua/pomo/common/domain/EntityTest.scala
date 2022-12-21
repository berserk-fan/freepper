package ua.pomo.common.domain

import cats.~>
import ua.pomo.common.domain
import ua.pomo.common.domain.registry.RegistryMapper5

import crud.{Crud, RepoOps, Repository}
import registry.Registry

case class EntityTest[F[_], G[_], T <: Crud](
                                              repository: domain.crud.Repository[F, T],
                                              generators: Generators[T],
                                              checkers: Assertions[T],
                                              crudOps: RepoOps[T],
                                              runner: F ~> G
)

object EntityTest {
  def ofRegistries[F[_], G[_]](
                                r1: Repository.Registry[F],
                                r2: Registry[Generators],
                                r3: Registry[Assertions],
                                r4: Registry[RepoOps],
                                r5: F ~> G
  ): Registry[Lambda[`T <: Crud` => EntityTest[F, G, T]]] = {
    Registry.map5(r1, r2, r3, r4, Registry.const[F ~> G](r5)) {
      new RegistryMapper5[
        Lambda[`T <: Crud` => Repository[F, T]],
        Generators,
        Assertions,
        RepoOps,
        Lambda[`X <: Crud` => F ~> G],
        Lambda[`X <: Crud` => EntityTest[F, G, X]]
      ] {
        override def apply[T <: Crud](
                                       f: Repository[F, T],
                                       f2: Generators[T],
                                       f3: Assertions[T],
                                       f4: RepoOps[T],
                                       f5: F ~> G
        ): EntityTest[F, G, T] = {
          EntityTest.apply(f, f2, f3, f4, f5)
        }
      }
    }
  }
}
