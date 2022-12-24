package ua.pomo.common.domain

import ua.pomo.common.domain.crud.Crud

trait Fixture[T <: Crud] {
  def entities: List[T#Create]
}
