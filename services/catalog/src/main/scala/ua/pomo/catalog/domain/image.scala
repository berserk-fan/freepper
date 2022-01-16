package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.derive
import eu.timepit.refined.util.string.uuid
import io.estatico.newtype.macros.newtype

import java.util.UUID


object image {
  @derive(eqv, show, uuid)
  case class ImageId(value: String)

  @derive(eqv, show)
  @newtype
  case class ImageSrc(value: String)

  @derive(eqv, show)
  @newtype
  case class ImageAlt(value: String)

  @derive(eqv, show)
  case class Image(id: ImageId, src: ImageSrc, alt: ImageAlt)

  @derive(eqv, show, uuid)
  @newtype
  case class ImageListId(uuid: UUID)

  @derive(eqv, show)
  @newtype
  case class ImageListName(value: String)

  @derive(eqv, show)
  case class ImageList(name: ImageListName, images: List[Image])

  trait ImageListRepository[F[_]] {
    def create(imageList: ImageList): F[ImageList]

    def findImageList(id: ImageListId): F[ImageList]

    def update(imageList: ImageList): F[ImageList]
  }

  trait ImageListService[F[_]] {
    def create(imageList: ImageList): F[ImageList]

    def findImageList(id: ImageListId): F[ImageList]

    def update(imageList: ImageList): F[ImageList]
  }
}
