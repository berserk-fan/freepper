package ua.pomo.catalog

import ua.pomo.common.config.JdbcDatabaseConfig

final case class ServerConfig(serverPort: Int)
final case class CatalogApiConfig(defaultPageSize: Int)
final case class AwsConfig(accessKeyId: String, secretAccessKey: String, imageBucketName: String, region: String)
final case class AppConfig(
    jdbc: JdbcDatabaseConfig,
    api: CatalogApiConfig,
    aws: AwsConfig,
    server: ServerConfig
)
