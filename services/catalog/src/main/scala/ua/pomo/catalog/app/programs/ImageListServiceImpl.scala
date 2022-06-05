package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.effect.{Async, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps}
import cats.~>
import doobie.{ConnectionIO, Transactor}
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.infrastructure.persistance.InMemoryImageListRepositoryImpl

class ImageListServiceImpl[F[_]: Async, G[_]: Sync] private (xa: G ~> F, repository: ImageListRepository[G])
    extends ImageListService[F] {
  override def create(imageList: ImageList): F[ImageList] = {
    repository.create(imageList).flatMap(repository.get).mapK(xa)
  }

  override def get(id: ImageListId): F[ImageList] = {
    repository.get(id).mapK(xa)
  }

  override def update(imageList: ImageListUpdate): F[ImageList] = {
    repository
      .update(imageList)
      .flatMap { _ =>
        repository.get(imageList.id)
      }
      .mapK(xa)
  }

  override def delete(imageListId: ImageListId): F[Unit] = {
    repository
      .delete(imageListId)
      .flatMap { deleted =>
        if (deleted == 0) {
          NotFound("imageList", imageListId).raiseError[G, Unit]
        } else {
          ().pure[G]
        }
      }
      .mapK(xa)
  }
}

object ImageListServiceImpl {
  def apply[F[_]: Async](trans: Transactor[F], repository: ImageListRepository[ConnectionIO]): ImageListService[F] = {
    new ImageListServiceImpl[F, ConnectionIO](trans.trans, repository)
  }

  def makeInMemory[F[_]: Async]: ImageListService[F] = {
    new ImageListServiceImpl[F, F](FunctionK.id[F], InMemoryImageListRepositoryImpl())
  }
}
