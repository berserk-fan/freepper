package ua.pomo.catalog

import cats.effect.{IO, Resource}
import io.grpc.Metadata
import org.scalatest.funsuite.AnyFunSuite
import ua.pomo.catalog.api.CatalogFs2Grpc
import ua.pomo.catalog.shared.{HasIOResource, Resources}

import java.util.UUID

class CatalogImplIT extends AnyFunSuite with HasIOResource {
  type Res = CatalogFs2Grpc[IO, Metadata]
  override protected val resource: Resource[IO, Res] = for {
    config <- Resources.config
    jdbcConfig = config.jdbc.copy(schema = UUID.randomUUID().toString)
    appConfig = config.copy(jdbc = jdbcConfig)
    transactor <- Resources.transactor(jdbcConfig)
    _ <- Resources.schema(jdbcConfig, transactor)
    _ <- Server.resource(appConfig)
    client <- Resources.catalogClient(appConfig.server.serverPort + 1)
  } yield client
}
