package com.freepper.common

import cats.effect.Sync
import cats.implicits.{toBifunctorOps, toFlatMapOps, toFunctorOps}
import pureconfig._

object AppConfigLoader {
  def loadDefault[F[_]: Sync, T: ConfigReader](namespace: String, subpath: Option[String] = None): F[T] = {
    Sync[F]
      .blocking(ConfigSource.resources(s"$namespace/application.conf"))
      .map(_.at(namespace))
      .map(configSource => subpath.fold(configSource)(subPath => configSource.at(subPath)))
      .map(_.load[T].leftMap(e => new Exception(e.prettyPrint())))
      .flatMap(Sync[F].fromEither(_))
  }
}
