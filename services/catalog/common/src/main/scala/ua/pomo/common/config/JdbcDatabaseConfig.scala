package ua.pomo.common.config

import pureconfig.ConfigReader

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
    migrationsTable: String,
    migrationsLocations: List[String],
    schema: String
)

object JdbcDatabaseConfig {
  implicit val configReader: ConfigReader[JdbcDatabaseConfig] =
    ConfigReader.forProduct7("url", "driver", "user", "password", "migrations-table", "migrations-locations", "schema")(
      JdbcDatabaseConfig.apply
    )
}
