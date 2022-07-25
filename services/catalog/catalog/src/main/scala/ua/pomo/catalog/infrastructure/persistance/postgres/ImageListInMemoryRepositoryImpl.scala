package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import monocle.syntax.AppliedLens
import monocle.syntax.all._
import shapeless._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}

import java.util.UUID

class ImageListInMemoryRepositoryImpl[F[_]: MonadThrow] private (mapRef: Ref[F, Map[ImageListId, ImageList]])
    extends AbstractInMemoryRepository[F, ImageListCrud](mapRef) {
  override protected def creator: ImageList => ImageList = _.copy(id = ImageListId(UUID.randomUUID()))

  override protected def filter: ImageListSelector => ImageList => Boolean = {
    case ImageListSelector.IdsIn(ids) =>
      (i: ImageList) => ids.toList.toSet.contains(i.id)
    case ImageListSelector.All =>
      (_: ImageList) => true
  }

  private object updateObj extends InMemoryUpdaterPoly[ImageList] {
    implicit val a: Res[ImageListDisplayName] = gen(_.focus(_.displayName))
    implicit val b: Res[List[ImageId]] =
      gen(x =>
        AppliedLens[ImageList, List[ImageId]](
          x,
          monocle.Lens[ImageList, List[ImageId]](_.images.map(_.id))(value =>
            imageList => imageList.copy(images = value.map(id => Image(id, ImageSrc(""), ImageAlt(""))))
          )
        )
      )
  }

  override def update(req: ImageListUpdate): F[Int] = mapRef.modify { map =>
    val updater = Generic[ImageListUpdate].to(req).drop(Nat._1).map(updateObj).toList.flatten.reduce(_ andThen _)
    (map.updatedWith(req.id)(_.map(updater)), if (map.contains(req.id)) 1 else 0)
  }
}

object ImageListInMemoryRepositoryImpl {
  def apply[F[_]: Sync](): ImageListRepository[F] = {
    val ref = Ref.unsafe[F, Map[ImageListId, ImageList]](Map())
    new ImageListInMemoryRepositoryImpl[F](ref)
  }
}
