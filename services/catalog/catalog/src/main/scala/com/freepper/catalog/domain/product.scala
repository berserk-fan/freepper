package com.freepper.catalog.domain

import cats.data.NonEmptyList




import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{Crud, Query, RepoOps, Repository}
import com.freepper.catalog.domain.category.CategoryId
import com.freepper.catalog.domain.imageList.{ImageList, ImageListId}
import com.freepper.catalog.domain.model.{ModelDisplayName, ModelId}
import com.freepper.catalog.domain.parameter._

import java.util.UUID

object product {


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
  type ProductQuery = Query[ProductSelector]

  sealed trait ProductSelector

  object ProductSelector {
    final case object All extends ProductSelector

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

  type ProductRepository[F[_]] = Repository[F, ProductCrud]

  type ProductCrud = Crud.type
  object Crud extends Crud {
    override type Create = CreateProduct
    override type Update = UpdateProduct
    override type Entity = Product
    override type EntityId = ProductId
    override type Selector = ProductSelector
    implicit val ops: RepoOps[ProductCrud] = new RepoOps[ProductCrud] {
      override def getIdUpdate(update: UpdateProduct): ProductId = update.id

      override def getIdEntity(entity: Product): ProductId = entity.id

      override def entityDisplayName: crud.EntityDisplayName = Entity.Product.name

      override def getIdCreate(create: CreateProduct): ProductId = create.id
    }
  }

}
