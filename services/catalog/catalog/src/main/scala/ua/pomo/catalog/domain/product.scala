package ua.pomo.catalog.domain

import cats.data.NonEmptyList
import derevo.cats.{eqv, show}
import derevo.circe.magnolia.decoder
import derevo.derive
import io.estatico.newtype.macros.newtype
import ua.pomo.catalog.domain.category.CategoryId
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListId}
import ua.pomo.catalog.domain.model.{ModelDisplayName, ModelId}
import ua.pomo.catalog.domain.parameter._
import ua.pomo.common.domain.crud
import ua.pomo.common.domain.crud.{Crud, PageToken, Query, RepoOps, Repository, ServiceOps}

import java.util.UUID

object product {
  @derive(eqv, show, decoder)
  @newtype
  case class ProductId(value: UUID)

  @derive(eqv, show, decoder)
  @newtype
  case class ProductStandardPrice(value: Double)

  @derive(eqv, show, decoder)
  @newtype
  case class ProductPromoPrice(value: Double)

  @derive(eqv, show, decoder)
  @newtype
  case class ProductDisplayName(value: String)

  @derive(eqv, show, decoder)
  case class ProductPrice(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

  @derive(eqv, show, decoder)
  case class ProductParameter(standard: ProductStandardPrice, promo: Option[ProductPromoPrice])

  @derive(eqv, show, decoder)
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

  @derive(eqv, show)
  case class CreateProduct(
      id: Option[ProductId],
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

  @derive(eqv, show)
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

      override def getIdCreate(update: CreateProduct): Option[ProductId] = update.id
    }
  }

}
