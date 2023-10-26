package com.freepper.catalog.domain.image

import com.freepper.catalog.domain.category.CreateCategory
import com.freepper.common.domain.crud.{Crud, Repository}
import com.freepper.common.domain.crud

import java.util.UUID

case class ImageId(value: UUID)

case class ImageSrc(value: String)

case class ImageAlt(value: String)

case class Image(id: ImageId, src: ImageSrc, alt: ImageAlt)

case class ImageData(value: Array[Byte])

case class CreateImage(id: ImageId, src: ImageSrc, alt: ImageAlt, data: ImageData)
case class CreateImageData(src: ImageSrc, data: ImageData)
case class BuzzImageUpdate(id: ImageId)

sealed trait ImageSelector
object ImageSelector {
  case object All extends ImageSelector
  case class IdIs(id: ImageId) extends ImageSelector
}

trait ImageDataRepository[F[_]] {
  def create(image: CreateImageData): F[Unit]
  def delete(src: ImageSrc): F[Unit]
  def list(prefix: String): F[List[ImageSrc]]
}

import Crud.*
type ImageRepository[F[_]] = Repository[F, ImageCrud]
type ImageQuery = crud.Query[ImageSelector]
type ImageCrud[X] = X match {
  case Create   => CreateImage
  case Update   => BuzzImageUpdate
  case Entity   => Image
  case EntityId => ImageId
  case Query    => ImageQuery
  case CrudName => "image"
}
