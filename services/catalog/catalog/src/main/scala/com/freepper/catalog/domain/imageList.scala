package com.freepper.catalog.domain

import cats.data.NonEmptyList




import com.freepper.common.domain.crud.{Crud, EntityDisplayName, Query, RepoOps, Repository}
import com.freepper.catalog.domain.image._

import java.util.UUID

object imageList {



  case class ImageListId(uuid: UUID)



  case class ImageListDisplayName(value: String)


  case class ImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[Image])


  case class CreateImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[ImageId])


  case class UpdateImageList(id: ImageListId, displayName: Option[ImageListDisplayName], images: Option[List[ImageId]])

  type ImageListQuery = Query[ImageListSelector]

  sealed trait ImageListSelector
  object ImageListSelector {
    final case object All extends ImageListSelector
    final case class IdsIn(ids: NonEmptyList[ImageListId]) extends ImageListSelector
  }

  type ImageListCrud = Crud.type

  object Crud extends Crud {
    override type Create = CreateImageList
    override type Update = UpdateImageList
    override type Entity = ImageList
    override type EntityId = ImageListId
    override type Selector = ImageListSelector
    implicit val ops: RepoOps[ImageListCrud] = new RepoOps[ImageListCrud] {

      override val entityDisplayName: EntityDisplayName = Entity.ImageList.name

      override def getIdUpdate(update: UpdateImageList): ImageListId = update.id

      override def getIdEntity(entity: ImageList): ImageListId = entity.id

      override def getIdCreate(create: CreateImageList): ImageListId = create.id
    }
  }

  type ImageListRepository[F[_]] = Repository[F, ImageListCrud]
}
