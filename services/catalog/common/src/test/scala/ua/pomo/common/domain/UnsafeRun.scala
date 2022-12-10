package ua.pomo.common.domain

import cats.effect.{IO, unsafe}

trait UnsafeRun[F[_]] {
  def unsafeRunSync[T](f: F[T]): T
}

object UnsafeRun {
  def apply[F[_]: UnsafeRun]: UnsafeRun[F] = implicitly[UnsafeRun[F]]

  implicit def unsafeIO(implicit runtime: unsafe.IORuntime): UnsafeRun[IO] = new UnsafeRun[IO] {
    override def unsafeRunSync[T](f: IO[T]): T = f.unsafeRunSync()
  }
}
