package ua.pomo.catalog.infrastructure.persistance

import cats.MonadThrow
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import ua.pomo.catalog.domain.image._

import java.util.UUID
import scala.collection.mutable

class InMemoryImageListRepositoryImpl[F[_]: MonadThrow] private(var mapRef: Ref[F, mutable.Map[ImageListId, ImageList]]) extends ImageListRepository[F] {
  override def create(imageList: ImageList): F[ImageListId] = mapRef.modify { map =>
    val id = ImageListId(UUID.randomUUID())
    map.addOne(id -> imageList.copy(id = id))
    (map, id)
  }

  override def get(id: ImageListId): F[ImageList] = {
    find(id)
      .flatMap(_.fold(new Exception("not found").raiseError[F, ImageList])(_.pure[F]))
  }

  override def find(id: ImageListId): F[Option[ImageList]] = mapRef.get.map(_.get(id))

  override def update(update: ImageListUpdate): F[Int] = mapRef.modify { map =>
    val updated = map.updateWith(update.id) {
      case None => None
      case Some(v) =>
        var newVal = v.copy()
        update.displayName.foreach { x => newVal = newVal.copy(displayName = x) }
        update.images.foreach { x => newVal = newVal.copy(images = x) }
        Some(newVal)
    }
    (map, updated.size)
  }


  override def delete(id: ImageListId): F[Int] = mapRef.modify { map =>
    val removed = map.remove(id)
    (map, removed.size)
  }
}

object InMemoryImageListRepositoryImpl {
  def apply[F[_]: Sync](): ImageListRepository[F] = {
    val ref = Ref.unsafe[F, mutable.Map[ImageListId, ImageList]](mutable.Map())
    new InMemoryImageListRepositoryImpl[F](ref)
  }
}
