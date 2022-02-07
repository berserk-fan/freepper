package ua.pomo.catalog.domain

import cats.data.NonEmptyList
import derevo.cats.{eqv, show}
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.param._
import ua.pomo.catalog.domain.image.{ImageList, ImageListId}
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}

import java.util.UUID

object product {
  @derive(eqv, show)
  @newtype
  case class ProductId(value: UUID)

  @derive(eqv, show)
  @newtype
  case class ProductDisplayName(value: String)

  @derive(eqv, show)
  @newtype
  case class ProductStandardPrice(value: Double)

  @derive(eqv, show)
  @newtype
  case class ProductPromoPrice(value: Double)

  @derive(eqv, show)
  sealed abstract class ProductParameterKind(val id: String)
  object ProductParameterKind {
    case object Size extends ProductParameterKind("size")
    case object Fabric extends ProductParameterKind("fabric")
  }

  @derive(eqv, show)
  case class ProductPrice(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

  type ProductParameters = Map[ProductParameterKind, Parameter]

  @derive(eqv, show)
  case class Product(id: ProductId,
                     modelId: ModelId,
                     categoryId: CategoryUUID,
                     displayName: ProductDisplayName,
                     imageList: ImageList,
                     price: ProductPrice,
                     parameters: Map[ProductParameterKind, Parameter])

  object Product {
    def createDisplayName(m: ModelDisplayName, parameters: List[Parameter]): ProductDisplayName = {
      ProductDisplayName(s"${m.value} ${parameters.map(_.displayName.value).mkString(" ")}")
    }
  }

  @derive(eqv, show)
  case class CreateProduct(id: ProductId,
                           modelId: ModelId,
                           imageListId: ImageListId,
                           priceUsd: ProductStandardPrice,
                           promoPriceUsd: Option[ProductPromoPrice],
                           parameters: Map[ProductParameterKind, ParameterId])

  case class ProductQuery(pageToken: PageToken.NonEmpty, selector: ProductSelector)

  sealed trait ProductSelector

  object ProductSelector {
    final case object All extends ProductSelector

    final case class IdIs(id: ProductId) extends ProductSelector

    final case class IdIn(ids: NonEmptyList[ProductId]) extends ProductSelector

    final case class ModelIs(modelId: ModelId) extends ProductSelector
  }

  @derive(eqv, show)
  case class UpdateProduct(id: ProductId,
                           modelId: Option[ModelId],
                           imageList: Option[ImageListId],
                           price: Option[ProductStandardPrice],
                           promoPrice: Option[Option[ProductPromoPrice]],
                           parameters: Option[Map[ProductParameterKind, ParameterId]])

  trait ProductRepository[F[_]] {
    def create(command: CreateProduct): F[ProductId]

    def get(id: ProductId): F[Product]

    def find(id: ProductId): F[Option[Product]]

    def query(query: ProductQuery): F[List[Product]]

    def update(command: UpdateProduct): F[Int]

    def delete(id: ProductId): F[Unit]
  }

  trait ProductService[F[_]] {
    def create(command: CreateProduct): F[Product]

    def get(id: ProductId): F[Product]

    def query(query: ProductQuery): F[List[Product]]

    def update(command: UpdateProduct): F[Product]

    def delete(id: ProductId): F[Unit]
  }
}
