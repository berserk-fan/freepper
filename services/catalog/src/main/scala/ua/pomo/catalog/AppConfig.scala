package ua.pomo.catalog

import cats.MonadThrow
import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._
import cats.implicits._

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

  def loadDefault[F[_]: Sync]: F[AppConfig] =
    Sync[F]
      .blocking(Source.fromResource("application.conf").mkString)
      .flatMap(loadFromString[F](_))

  private def loadFromString[F[_]: MonadThrow](configStr: String): F[AppConfig] = MonadThrow[F].fromEither {
    ConfigSource
      .string(configStr)
      .at(namespace)
      .load[AppConfig]
      .leftMap(e => new Exception(e.prettyPrint()))
  }

  //TODO: use blocking
  def loadFromFile[F[_]: MonadThrow](file: File): F[AppConfig] =
    MonadThrow[F]
      .catchNonFatal(Files.readString(file.toPath))
      .flatMap(loadFromString[F](_))
}
