package ua.pomo.common.domain

import cats.~>
import doobie.ConnectionIO
import ua.pomo.common.config.JdbcDatabaseConfig

trait DbModule[F[_]] {
  def jdbcConfig: JdbcDatabaseConfig
  def transactor: ConnectionIO ~> F
  def schema: Schema
}
