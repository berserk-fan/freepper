package com.freepper.common.domain

import com.freepper.common.domain.crud.Crud

trait Fixture[T <: Crud] {
  def entities: List[T#Create]
}
