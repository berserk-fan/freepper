package ua.pomo.catalog

import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._
import cats.implicits._

import java.io.File

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: Option[String],
    password: Option[String],
    migrationsTable: String,
    migrationsLocations: List[String]
)

final case class ServerConfig(numThreads: Int, serverPort: Int)

final case class AppConfig(jdbc: JdbcDatabaseConfig, server: ServerConfig)

object AppConfig {
  private val namespace = "pomo"

  def loadDefault[F[_]: Sync]: F[AppConfig] = {
    findApplicationConfFile().flatMap { appConfFile =>
      loadFromFile(appConfFile)
    }
  }

  private def findApplicationConfFile[F[_]: Sync](): F[File] = Sync[F].blocking {
    new File(getClass.getClassLoader.getResource("application.conf").toURI)
  }

  def loadFromFile[F[_]: Sync](file: File): F[AppConfig] = Sync[F].fromEither {
    ConfigSource
      .file(file)
      .at(namespace)
      .load[AppConfig]
      .leftMap(e => new Exception(e.prettyPrint()))
  }
}
