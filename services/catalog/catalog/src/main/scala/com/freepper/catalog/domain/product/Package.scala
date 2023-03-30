package com.freepper.catalog.domain.product

import cats.data.NonEmptyList
import com.freepper.common.domain.crud
import com.freepper.catalog.domain.category.CategoryId
import com.freepper.catalog.domain.imageList.{ImageList, ImageListId}
import com.freepper.catalog.domain.model.{ModelDisplayName, ModelId}
import com.freepper.catalog.domain.parameter.*
import com.freepper.common.domain.crud.{Crud, Repository}

import java.util.UUID

case class ProductId(value: UUID)

case class ProductStandardPrice(value: Double)

case class ProductPromoPrice(value: Double)

case class ProductDisplayName(value: String)

case class ProductPrice(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

case class ProductParameter(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

case class Product(
    id: ProductId,
    modelId: ModelId,
    displayName: ProductDisplayName,
    categoryId: CategoryId,
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

case class CreateProduct(
    id: ProductId,
    modelId: ModelId,
    imageListId: ImageListId,
    priceUsd: ProductStandardPrice,
    promoPriceUsd: Option[ProductPromoPrice],
    parameterIds: List[ParameterId]
)

sealed trait ProductSelector

object ProductSelector {
  case object All extends ProductSelector

  final case class IdIs(id: ProductId) extends ProductSelector

  final case class IdIn(ids: NonEmptyList[ProductId]) extends ProductSelector

  final case class ModelIs(modelId: ModelId) extends ProductSelector
}

case class UpdateProduct(
    id: ProductId,
    imageListId: Option[ImageListId],
    price: Option[ProductStandardPrice],
    promoPrice: Option[Option[ProductPromoPrice]]
)

import Crud.*
type ProductQuery = crud.Query[ProductSelector]
type ProductCrud[X] = X match {
  case Create   => CreateProduct
  case Update   => UpdateProduct
  case Entity   => Product
  case EntityId => ProductId
  case Query    => ProductQuery
}

type ProductRepository[F[_]] = Repository[F, ProductCrud]
