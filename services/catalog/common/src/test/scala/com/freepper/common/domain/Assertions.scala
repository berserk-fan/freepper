package com.freepper.common.domain

import .Crud
import Crud.*

trait Assertions[C[_]] {
  def update(c: C[Update], v: C[Entity]): Any
  def create(c: C[Create], v: C[Entity]): Any
}
