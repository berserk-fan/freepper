package ua.pomo.catalog.shared

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doobie.Transactor

case class DbResources(xa: Transactor[IO], runtime: IORuntime, schema: Schema)
trait HasDbResources {
  def db: DbResources
}
