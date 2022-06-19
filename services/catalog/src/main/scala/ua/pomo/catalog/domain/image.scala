package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype

import java.util.UUID

object image {
  @derive(eqv, show, decoder)
  @newtype
  case class ImageId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ImageSrc(value: String)

  @derive(eqv, show, decoder)
  @newtype
  case class ImageAlt(value: String)

  @derive(eqv, show, decoder)
  case class Image(id: ImageId, src: ImageSrc, alt: ImageAlt)

  case class ImageData(value: Array[Byte])

  case class CreateImage(src: ImageSrc, alt: ImageAlt, data: ImageData)
  case class CreateImageMetadata(src: ImageSrc, alt: ImageAlt)
  case class CreateImageData(src: ImageSrc, data: ImageData)

  sealed trait ImageSelector
  object ImageSelector {
    case object All extends ImageSelector
    case class IdIs(id: ImageId) extends ImageSelector
  }

  case class ImageQuery(selector: ImageSelector, page: PageToken.NonEmpty)

  @derive(eqv, show)
  case class FindImagesResponse(images: List[Image], nextPageToken: PageToken)

  trait ImageDataRepository[F[_]] {
    def create(image: CreateImageData): F[Unit]
    def delete(src: ImageSrc): F[Unit]
    def list(prefix: String): F[List[ImageSrc]]
  }

  trait ImageRepository[F[_]] {
    def create(image: CreateImageMetadata): F[ImageId]
    def get(id: ImageId): F[Image]
    def query(query: ImageQuery): F[List[Image]]
    def delete(imageId: ImageId): F[Int]
  }

  trait ImageService[F[_]] {
    def create(image: CreateImage): F[Image]
    def get(id: ImageId): F[Image]
    def delete(imageId: ImageId): F[Unit]
    def query(query: ImageQuery): F[FindImagesResponse]
  }
}
