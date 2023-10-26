package com.freepper.auth

import com.freepper.common.config.JdbcDatabaseConfig
import pureconfig.ConfigReader

case class AppConfig(jdbc: JdbcDatabaseConfig)

object AppConfig {
  implicit val q: ConfigReader[AppConfig] = ConfigReader.forProduct1("jdbc")(AppConfig.apply)
}
