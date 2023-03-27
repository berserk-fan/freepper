package com.freepper.common.domain

trait TypeName[C[_]] {
  def name: String
}
