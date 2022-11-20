package ua.pomo.common

import cats.effect.IO
import doobie.Transactor
import ua.pomo.common.domain.Schema

trait DbResources[F[_]] {
  def xa: Transactor[F]
  def schema: Schema
}
