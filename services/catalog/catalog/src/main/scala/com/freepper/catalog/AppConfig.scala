package com.freepper.catalog

import pureconfig.ConfigReader
import com.freepper.common.config.JdbcDatabaseConfig
import com.freepper.common.domain.auth.AuthConfig

final case class ServerConfig(serverPort: Int)

object ServerConfig {
  implicit val configReader: ConfigReader[ServerConfig] =
    ConfigReader.forProduct1("server-port")(ServerConfig.apply)
}

final case class CatalogApiConfig(defaultPageSize: Int)

object CatalogApiConfig {
  implicit val configReader: ConfigReader[CatalogApiConfig] =
    ConfigReader.forProduct1("default-page-size")(CatalogApiConfig.apply)
}

final case class AwsConfig(accessKeyId: String, secretAccessKey: String, imageBucketName: String, region: String)
object AwsConfig {
  implicit val configReader: ConfigReader[AwsConfig] =
    ConfigReader.forProduct4("access-key-id", "secret-access-key", "image-bucket-name", "region")(AwsConfig.apply)
}

final case class AppConfig(
    jdbc: JdbcDatabaseConfig,
    api: CatalogApiConfig,
    aws: AwsConfig,
    server: ServerConfig,
    auth: AuthConfig
)

object AppConfig {
  implicit val reader: ConfigReader[AppConfig] =
    ConfigReader.forProduct5("jdbc", "api", "aws", "server", "auth")(AppConfig.apply)
}
