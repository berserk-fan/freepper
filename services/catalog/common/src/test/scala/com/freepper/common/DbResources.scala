package com.freepper.common

import com.freepper.common.domain.Schema
import doobie.Transactor
import com.freepper.common.domain.Schema

trait DbResources[F[_]] {
  def xa: Transactor[F]
  def schema: Schema
}
