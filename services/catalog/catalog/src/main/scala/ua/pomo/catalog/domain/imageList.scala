package ua.pomo.catalog.domain

import cats.data.NonEmptyList
import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.image._
import ua.pomo.common.domain.crud.{Crud, EntityDisplayName, Query, RepoOps, Repository}

import java.util.UUID

object imageList {

  @derive(eqv, show, decoder)
  @newtype
  case class ImageListId(uuid: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ImageListDisplayName(value: String)

  @derive(eqv, show, decoder)
  case class ImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[Image])

  @derive(eqv, show)
  case class CreateImageList(id: ImageListId, displayName: ImageListDisplayName, images: List[ImageId])

  @derive(eqv, show)
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
