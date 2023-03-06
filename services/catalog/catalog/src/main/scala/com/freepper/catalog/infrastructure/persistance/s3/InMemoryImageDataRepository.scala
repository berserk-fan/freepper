package com.freepper.catalog.infrastructure.persistance.s3

import cats.Functor
import cats.effect.{IO, Ref}
import cats.implicits.toFunctorOps
import com.freepper.catalog.domain.image._

class InMemoryImageDataRepository[F[_]: Functor] private (state: Ref[F, Set[ImageSrc]]) extends ImageDataRepository[F] {
  override def create(image: CreateImageData): F[Unit] = state.update(_.+(image.src)).as(())
  override def delete(src: ImageSrc): F[Unit] = state.update(_.-(src)).as(())
  override def list(prefix: String): F[List[ImageSrc]] = state.get.map(_.filter(_.value.startsWith(prefix)).toList)
}
object InMemoryImageDataRepository {
  def apply(): IO[InMemoryImageDataRepository[IO]] = {
    Ref.of[IO, Set[ImageSrc]](Set()).map(new InMemoryImageDataRepository[IO](_))
  }
}
