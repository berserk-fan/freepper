package ua.pomo.common

import cats.effect.Async
import ua.pomo.common.config.JdbcDatabaseConfig
import doobie.Transactor

import java.util.Properties

object TransactorHelpers {
  def fromConfig[F[_]: Async](config: JdbcDatabaseConfig): Transactor[F] = {
    val props = new Properties()
    props.setProperty("user", config.user)
    props.setProperty("password", config.password)
    props.setProperty("options", s"-c search_path=${config.schema}")

    Transactor.fromDriverManager[F](
      config.driver,
      config.url,
      props
    )
  }
}
