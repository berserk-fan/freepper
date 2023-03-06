package com.freepper.common.domain

import com.freepper.common.domain.crud.Crud

trait Assertions[T <: Crud] {
  def update(c: T#Update, v: T#Entity): Any
  def create(c: T#Create, v: T#Entity): Any
}
