package com.freepper.common.domain

import .Crud.*

trait Fixture[C[_]] {
  def entities: List[C[Create]]
}
