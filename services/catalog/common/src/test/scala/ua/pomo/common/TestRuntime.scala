package ua.pomo.common

import cats.effect.unsafe.IORuntime

object TestRuntime {
  lazy val testRuntime: IORuntime = HasIORuntime.runtime
}
