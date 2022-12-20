package ua.pomo.common

import doobie.Transactor
import ua.pomo.common.domain.Schema

trait DbResources[F[_]] {
  def xa: Transactor[F]
  def schema: Schema
}
