package ua.pomo.catalog.domain

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image.ImageList
import ua.pomo.catalog.optics.uuid

import java.util.UUID

object model {
  type ModelId = ModelUUID Either ModelReadableId

  object ModelId {
    def apply(id: ModelUUID): ModelId = Left(id)

    def apply(id: ModelReadableId)(implicit d: DummyImplicit): ModelId = Right(id)
  }

  @derive(eqv, show, uuid)
  @newtype
  case class ModelUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ModelReadableId(value: String)

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
                         description: Option[ModelDescription],
                         imageList: Option[ImageList])

  @derive(eqv, show)
  case class FindModel(categoryUUID: CategoryUUID,
                       limit: Int,
                       offset: Int)

  trait ModelRepository[F[_]] {
    def create(category: Model): F[ModelUUID]

    def get(id: ModelId): F[Model]

    def find(id: ModelId): F[Option[Model]]

    def findAll(req: FindModel): F[List[Model]]

    def update(req: UpdateModel): F[Int]

    def delete(id: ModelId): F[Int]
  }
}
