package ua.pomo.common.domain

import ua.pomo.common.domain.repository.Crud

trait RepoTestRegistry[F[_]] {
  def value: List[EntityTest[F, _ <: Crud]]
}
