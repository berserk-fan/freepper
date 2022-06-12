package ua.pomo.catalog.domain

import cats.data.NonEmptyList
import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype

import java.util.UUID

object image {
  @derive(eqv, show, decoder)
  @newtype
  case class ImageSrc(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ImageAlt(value: String)

  @derive(eqv, show, decoder)
  case class Image(src: ImageSrc, alt: ImageAlt)

  @derive(eqv, show, decoder)
  @newtype
  case class ImageListId(uuid: UUID)

  @derive(eqv, show)
  @newtype
  case class ImageListDisplayName(value: String)

  @derive(eqv, show)
  case class ImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[Image])

  @derive(eqv, show)
  case class ImageListUpdate(id: ImageListId, displayName: Option[ImageListDisplayName], images: Option[List[Image]])

  case class ImageListQuery(selector: ImageListSelector, page: PageToken.NonEmpty)

  @derive(eqv, show)
  case class FindImageListResponse(imageLists: List[ImageList], nextPageToken: PageToken)

  sealed trait ImageListSelector
  object ImageListSelector {
    final case object All extends ImageListSelector
    final case class IdsIn(ids: NonEmptyList[ImageListId]) extends ImageListSelector
  }

  trait ImageListRepository[F[_]] {
    def create(imageList: ImageList): F[ImageListId]
    def get(id: ImageListId): F[ImageList]
    def find(id: ImageListId): F[Option[ImageList]]
    def query(query: ImageListQuery): F[List[ImageList]]
    def update(imageList: ImageListUpdate): F[Int]
    def delete(imageListId: ImageListId): F[Int]
  }

  trait ImageListService[F[_]] {
    def create(imageList: ImageList): F[ImageList]
    def get(id: ImageListId): F[ImageList]
    def update(imageList: ImageListUpdate): F[ImageList]
    def delete(imageListId: ImageListId): F[Unit]
    def find(query: ImageListQuery): F[FindImageListResponse]
  }
}
