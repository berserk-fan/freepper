package ua.pomo.catalog.shared

import cats.effect.unsafe
import cats.effect.unsafe.IORuntime

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait HasIORuntime {
  implicit protected def runtime: IORuntime = HasIORuntime.runtime
}

object HasIORuntime {
  private lazy val runtime = {
    val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
    val (blocking, blockingSD) = unsafe.IORuntime.createDefaultBlockingExecutionContext()
    val (scheduler, schedulerSD) = unsafe.IORuntime.createDefaultScheduler()
    unsafe.IORuntime(
      ec,
      blocking,
      scheduler,
      { () =>
        blockingSD(); schedulerSD();
      },
      unsafe.IORuntimeConfig()
    )
  }
}
