package ua.pomo.catalog.infrastructure.persistance.inmemory

import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFunctorOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import ua.pomo.catalog.domain.image._

import java.util.UUID
import scala.collection.mutable

class InMemoryImageListRepositoryImpl private(var mapRef: Ref[ConnectionIO, mutable.Map[ImageListId, ImageList]]) extends ImageListRepository[ConnectionIO] {
  override def create(imageList: ImageList): doobie.ConnectionIO[ImageListId] = mapRef.modify { map =>
    val id = ImageListId(UUID.randomUUID())
    map.addOne(id -> imageList.copy(id = id))
    (map, id)
  }

  override def get(id: ImageListId): doobie.ConnectionIO[ImageList] = {
    find(id)
      .flatMap(_.fold(new Exception("not found").raiseError[ConnectionIO, ImageList])(_.pure[ConnectionIO]))
  }

  override def find(id: ImageListId): doobie.ConnectionIO[Option[ImageList]] = mapRef.get.map(_.get(id))

  override def update(update: ImageListUpdate): doobie.ConnectionIO[Int] = mapRef.modify { map =>
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


  override def delete(id: ImageListId): doobie.ConnectionIO[Int] = mapRef.modify { map =>
    val removed = map.remove(id)
    (map, removed.size)
  }
}

object InMemoryImageListRepositoryImpl {
  def apply(): ImageListRepository[ConnectionIO] = {
    val ref = Ref.unsafe[ConnectionIO, mutable.Map[ImageListId, ImageList]](mutable.Map())
    new InMemoryImageListRepositoryImpl(ref)
  }
}
