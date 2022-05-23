package ua.pomo.catalog.domain

import cats.data.NonEmptyList
import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.image.{ImageList, ImageListId}
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}

import java.util.UUID

object product {
  @derive(eqv, show)
  @newtype
  case class ProductId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ProductStandardPrice(value: Double)

  @derive(eqv, show)
  @newtype
  case class ProductPromoPrice(value: Double)

  @derive(eqv, show)
  @newtype
  case class ProductDisplayName(value: String)

  @derive(eqv, show)
  case class ProductPrice(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

  @derive(eqv, show)
  case class ProductParameter(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

  @derive(eqv, show)
  case class Product(
      id: ProductId,
      modelId: ModelId,
      displayName: ProductDisplayName,
      categoryId: CategoryUUID,
      imageList: ImageList,
      price: ProductPrice,
      parameterIds: List[ParameterId]
  )

  object Product {
    def makeDisplayName(
        modelDisplayName: ModelDisplayName,
        parameterDisplayNames: List[ParameterDisplayName]
    ): ProductDisplayName = {
      val paramNames = parameterDisplayNames.sortBy(_.value).mkString(" ")
      ProductDisplayName(s"$modelDisplayName $paramNames")
    }
  }

  @derive(eqv, show)
  case class CreateProduct(
      modelId: ModelId,
      imageListId: ImageListId,
      priceUsd: ProductStandardPrice,
      promoPriceUsd: Option[ProductPromoPrice],
      parameterIds: List[ParameterId]
  )

  @derive(eqv, show)
  case class FindProductResponse(products: List[Product], nextPageToken: PageToken)

  case class ProductQuery(pageToken: PageToken.NonEmpty, selector: ProductSelector)

  sealed trait ProductSelector

  object ProductSelector {
    final case object All extends ProductSelector

    final case class IdIs(id: ProductId) extends ProductSelector

    final case class IdIn(ids: NonEmptyList[ProductId]) extends ProductSelector

    final case class ModelIs(modelId: ModelId) extends ProductSelector
  }

  @derive(eqv, show)
  case class UpdateProduct(
      id: ProductId,
      imageList: Option[ImageListId],
      price: Option[ProductStandardPrice],
      promoPrice: Option[Option[ProductPromoPrice]]
  )

  trait ProductRepository[F[_]] {
    def create(command: CreateProduct): F[ProductId]

    def get(id: ProductId): F[Product]

    def find(id: ProductId): F[Option[Product]]

    def query(query: ProductQuery): F[List[Product]]

    def update(command: UpdateProduct): F[Int]

    def delete(id: ProductId): F[Int]
  }

  trait ProductService[F[_]] {
    def create(command: CreateProduct): F[Product]

    def get(id: ProductId): F[Product]

    def query(query: ProductQuery): F[FindProductResponse]

    def update(command: UpdateProduct): F[Product]

    def delete(id: ProductId): F[Unit]
  }
}
