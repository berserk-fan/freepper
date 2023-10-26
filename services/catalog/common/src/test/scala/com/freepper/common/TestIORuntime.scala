package com.freepper.common

import cats.effect.unsafe
import cats.effect.unsafe.IORuntime

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait TestIORuntime {
  implicit protected def runtime: IORuntime = TestIORuntime.runtime
}

object TestIORuntime {
  lazy implicit val runtime: IORuntime = {
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
