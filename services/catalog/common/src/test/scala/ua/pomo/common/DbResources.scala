package ua.pomo.common

import cats.effect.IO
import doobie.Transactor

case class DbResources(xa: Transactor[IO], schema: Schema)
