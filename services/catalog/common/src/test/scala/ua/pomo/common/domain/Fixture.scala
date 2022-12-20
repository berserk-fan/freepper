package ua.pomo.common.domain

import ua.pomo.common.domain.repository.Crud

trait Fixture[T <: Crud] {
  def entities: List[T#Create]
}

