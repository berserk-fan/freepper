package com.freepper.common.domain

import com.freepper.common.domain.crud.Crud.*

trait Fixture[C[_]] {
  def entities: List[C[Create]]
}
