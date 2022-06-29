package ua.pomo.catalog.shared

import cats.effect.IO
import doobie.Transactor

case class DbResources(xa: Transactor[IO], schema: Schema)
