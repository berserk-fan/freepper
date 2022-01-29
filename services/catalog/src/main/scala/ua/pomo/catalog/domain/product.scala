package ua.pomo.catalog.domain

import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import squants.market.Money
import ua.pomo.catalog.domain.image.{Image, ImageList, ImageListId}
import ua.pomo.catalog.domain.model.ModelUUID

import java.util.UUID

object product {
  @derive(eqv, show)
  @newtype
  case class ProductUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ProductDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class ProductDescription(value: String)

  @derive(eqv, show)
  @newtype
  case class FabricUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class FabricDisplayName(value: String)

  @derive(eqv, show)
  case class Fabric(id: FabricUUID, displayName: FabricDisplayName, image: Image)

  @derive(eqv, show)
  @newtype
  case class SizeUUID(value: UUID)

  @derive(eqv, show)
  @newtype
  case class SizeDisplayName(value: String)

  @derive(eqv, show)
  case class Size(uuid: SizeUUID, displayName: SizeDisplayName)

  @derive(eqv, show)
  @newtype
  case class ProductStandardPrice(value: Money)

  @derive(eqv, show)
  @newtype
  case class ProductPromoPrice(value: Money)

  @derive(eqv, show)
  case class ProductPrice(standard: ProductStandardPrice, promo: ProductPromoPrice)

  @derive(eqv, show)
  case class Product(
      uuid: ProductUUID,
      modelId: ModelUUID,
      displayName: ProductDisplayName,
      fabric: Fabric,
      size: Size,
      imageList: ImageList,
      price: ProductPrice
  )

  @derive(eqv, show)
  case class CreateProduct(
      id: ProductUUID,
      modelId: ModelUUID,
      imageListId: ImageListId,
      displayName: ProductDisplayName,
      description: ProductDescription,
      fabric: Fabric,
      size: Size,
      price: ProductPrice
  )

  @derive(eqv, show)
  case class FindProduct(modelId: ModelUUID, pageToken: PageToken)
  @derive(eqv, show)
  case class UpdateProduct(
      uuid: ProductUUID,
      modelId: Option[ModelUUID],
      displayName: Option[ProductDisplayName],
      description: Option[ProductDescription],
      fabric: Option[Fabric],
      size: Option[Size],
      imageList: Option[ImageListId],
      price: Option[ProductStandardPrice],
      promoPrice: Option[ProductPromoPrice]
  )

  trait ProductRepository[F[_]] {
    def create(model: CreateProduct): F[ProductUUID]

    def get(id: ProductUUID): F[Product]

    def find(id: ProductUUID): F[Option[Product]]

    def findAll(req: FindProduct): F[List[Product]]

    def update(req: UpdateProduct): F[Int]

    def delete(id: ProductUUID): F[Unit]
  }

}
