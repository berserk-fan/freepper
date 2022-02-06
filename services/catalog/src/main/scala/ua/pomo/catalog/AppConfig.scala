package ua.pomo.catalog

import cats.MonadThrow
import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._
import cats.implicits._
import com.typesafe.config.Config

import java.io.File
import java.nio.file.Files
import scala.io.Source

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
    migrationsTable: String,
    migrationsLocations: List[String],
    schema: String
)

final case class ServerConfig(serverPort: Int)
final case class CatalogApiConfig(defaultPageSize: Int)
final case class AppConfig(jdbc: JdbcDatabaseConfig, catalog: CatalogApiConfig, server: ServerConfig)

object AppConfig {
  private val namespace = "pomo"

  def loadDefault[F[_]: Sync]: F[AppConfig] = {
    Sync[F]
      .blocking(ConfigSource.resources("application.conf"))
      .map(_.at(namespace))
      .map(_.load[AppConfig].leftMap(e => new Exception(e.prettyPrint())))
      .flatMap(Sync[F].fromEither(_))
  }
}
