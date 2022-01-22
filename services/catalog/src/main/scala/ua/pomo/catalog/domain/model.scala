package ua.pomo.catalog.domain

import cats.{Eq, Show}
import derevo.cats._
import derevo.derive
import cats.implicits.toShow
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image.{ImageList, ImageListId}
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object model {
  private type ModelIdType = ModelUUID Either ModelReadableId
  sealed abstract case class ModelId private (value: ModelIdType)
  object ModelId {
    def apply(id: ModelUUID): ModelId = new ModelId(Left(id)) {}
    def apply(id: ModelReadableId)(implicit d: DummyImplicit): ModelId = new ModelId(Right(id)) {}
    implicit val show: Show[ModelId] = _.value.fold(_.show, _.show)
    implicit val eqv: Eq[ModelId] = _ == _
  }

  @derive(eqv, show, uuid)
  @newtype
  case class ModelUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ModelReadableId(value: ReadableId)

  @derive(eqv, show)
  @newtype
  case class ModelDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelDescription(value: String)

  @derive(eqv, show)
  @newtype
  case class ModelImageList(value: ImageList)

  @derive(eqv, show)
  @newtype
  case class ModelMinimalPrice(value: Money)

  @derive(eqv, show)
  case class Model(id: ModelUUID,
                   readableId: ModelReadableId,
                   categoryId: CategoryUUID,
                   displayName: ModelDisplayName,
                   description: ModelDescription,
                   minimalPrice: ModelMinimalPrice,
                   imageList: ImageList)


  @derive(eqv, show)
  case class UpdateModel(id: ModelUUID,
                         readableId: Option[ModelReadableId],
                         categoryId: Option[CategoryUUID],
                         displayName: Option[ModelDisplayName],
                         description: Option[ModelDescription],
                         imageListId: Option[ImageListId])

  @derive(eqv, show)
  case class FindModel(categoryUUID: CategoryUUID,
                       limit: Long,
                       offset: Long)

  trait ModelRepository[F[_]] {
    def create(model: Model): F[ModelUUID]

    def get(id: ModelId): F[Model]

    def find(id: ModelId): F[Option[Model]]

    def findAll(req: FindModel): F[List[Model]]

    def update(req: UpdateModel): F[Int]

    def delete(id: ModelId): F[Unit]
  }

  trait ModelService[F[_]] {
    def create(category: Model): F[Model]

    def get(id: ModelId): F[Model]

    def find(id: ModelId): F[Option[Model]]

    def findAll(req: FindModel): F[List[Model]]

    def update(req: UpdateModel): F[Model]

    def delete(id: ModelId): F[Unit]
  }
}
