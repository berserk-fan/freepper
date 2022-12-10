package ua.pomo.common.domain

import ua.pomo.common.domain.repository.Crud
import ua.pomo.common.domain.repository.CrudOps
import ua.pomo.common.domain

case class EntityTest[F[_], T <: Crud](
    repository: domain.repository.Repository[F, T],
    generators: Generators[T],
    checkers: Assertions[T],
    crudOps: CrudOps[T]
)
