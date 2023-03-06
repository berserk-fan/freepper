package com.freepper.app

import cats.effect.Sync
import com.freepper
import pureconfig.generic.auto._
import com.freepper.common.AppConfigLoader
import com.freepper.common.config.JdbcDatabaseConfig

sealed abstract class Service[Config <: Product](val name: String) {
  def jdbcConfig(c: Config): Option[JdbcDatabaseConfig]
}

object Service {
  case object Catalog extends Service[freepper.catalog.AppConfig]("catalog") {
    override def jdbcConfig(c: freepper.catalog.AppConfig): Option[JdbcDatabaseConfig] = Some(c.jdbc)
  }
  case object MainService extends Service[AppConfig]("app") {
    override def jdbcConfig(c: AppConfig): Option[JdbcDatabaseConfig] = None
  }
  val all: List[Service[_ <: Product]] = List(Catalog, MainService)
  def fromName(name: String): Option[Service[_ <: Product]] = all.find(_.name == name)
}

object ConfigLoader {
  case class PartiallyAppliedLoader[F[_]]() {
    def apply[T <: Product](app: Service[T])(implicit e: Sync[F]): F[T] = {
      app match {
        case Service.Catalog     => AppConfigLoader.loadDefault[F, freepper.catalog.AppConfig](app.name)
        case Service.MainService => AppConfigLoader.loadDefault[F, AppConfig](app.name)
      }
    }
  }

  def load[F[_]]: PartiallyAppliedLoader[F] = PartiallyAppliedLoader()
}
