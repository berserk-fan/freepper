package ua.pomo.common.config

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
    migrationsTable: String,
    migrationsLocations: List[String],
    schema: String
)
