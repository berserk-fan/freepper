package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.common.domain.crud.{Crud, EntityDisplayName, Query, RepoOps, Repository}

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

  case class CreateImage(id: ImageId, src: ImageSrc, alt: ImageAlt, data: ImageData)
  case class CreateImageData(src: ImageSrc, data: ImageData)
  case class BuzzImageUpdate(id: ImageId)

  type ImageQuery = Query[ImageSelector]
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

  type ImageRepository[F[_]] = Repository[F, Crud.type]
  type ImageCrud = Crud.type

  object Crud extends Crud {
    override type Create = CreateImage
    override type Update = BuzzImageUpdate
    override type Entity = Image
    override type EntityId = ImageId
    override type Selector = ImageSelector

    implicit val ops: RepoOps[ImageCrud] = new RepoOps[ImageCrud] {
      override def getIdUpdate(update: BuzzImageUpdate): ImageId = update.id

      override def getIdEntity(entity: Image): ImageId = entity.id

      override def entityDisplayName: EntityDisplayName = Entity.Image.name

      override def getIdCreate(create: CreateImage): ImageId = create.id
    }
  }
}
