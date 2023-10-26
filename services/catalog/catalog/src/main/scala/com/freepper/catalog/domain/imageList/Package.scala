package com.freepper.catalog.domain.imageList

import cats.data.NonEmptyList
import com.freepper.common.domain.crud
import com.freepper.catalog.domain.image.*
import com.freepper.common.domain.crud.{Crud, Repository}

import java.util.UUID

case class ImageListId(value: UUID)

case class ImageListDisplayName(value: String)

case class ImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[Image])

case class CreateImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[ImageId])

case class UpdateImageList(id: ImageListId, displayName: Option[ImageListDisplayName], images: Option[List[ImageId]])

sealed trait ImageListSelector

object ImageListSelector {
  case object All extends ImageListSelector

  case class IdsIn(ids: NonEmptyList[ImageListId]) extends ImageListSelector
}

type ImageListQuery = crud.Query[ImageListSelector]

import Crud.*
type ImageListCrud[X] = X match {
  case Create   => CreateImageList
  case Update   => UpdateImageList
  case Entity   => ImageList
  case EntityId => ImageListId
  case Query    => ImageListQuery
  case CrudName => "imageList"
}

type ImageListRepository[F[_]] = Repository[F, ImageListCrud]
