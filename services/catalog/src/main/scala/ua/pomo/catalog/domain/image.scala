package ua.pomo.catalog.domain

import derevo.cats.{ eqv, show }
import derevo.derive
import io.estatico.newtype.macros.newtype

object image {
  @derive(eqv, show)
  @newtype
  case class ImageSrc(value: String)

  @derive(eqv, show)
  @newtype
  case class ImageAlt(value: String)

  @derive(eqv, show)
  @newtype
  case class ImageObjectName(value: String)

  @derive(eqv, show)
  case class ImageData(src: ImageSrc, alt: ImageAlt, objectName: Option[ImageObjectName])
}
