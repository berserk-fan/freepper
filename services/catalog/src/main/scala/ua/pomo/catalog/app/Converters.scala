package ua.pomo.catalog.app

import cats.implicits.toShow
import io.circe.{Decoder, Encoder, parser}
import scalapb.FieldMaskUtil
import squants.market.Money
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{CreateCategoryRequest, CreateModelRequest, DeleteModelRequest, GetProductRequest, ListModelsRequest, ListModelsResponse, UpdateCategoryRequest}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.product._

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import scala.util.{Success, Try}

object Converters {
  def toApi(cat: Category): api.Category = {
    api.Category(
      CategoryName(cat.uuid).toNameString,
      cat.uuid.value.toString,
      cat.readableId.value,
      cat.displayName.value,
      cat.description.value
    )
  }

  private val utf8 = StandardCharsets.UTF_8
  private def toApi(pageToken: PageToken): String = {
    Base64.getEncoder.encodeToString {
      val res = pageToken match {
        case PageToken.Empty              => ""
        case x @ PageToken.NonEmpty(_, _) => Encoder[PageToken.NonEmpty].apply(x).show
      }
      res.getBytes(utf8)
    }
  }

  def toDomain(listModels: ListModelsRequest): Try[ModelQuery] = {
    val categoryId = ApiName.models(listModels.parent).toOption.get.categoryId
    Try(new String(Base64.getDecoder.decode(listModels.pageToken), utf8))
      .flatMap {
        case "" => Success(PageToken.NonEmpty(listModels.pageSize.toLong, 0L))
        case s =>
          parser.parse(s).toTry.flatMap(Decoder[PageToken.NonEmpty].decodeJson(_).toTry)
      }
      .map(ModelQuery(ModelSelector.CategoryIdIs(categoryId), _))
  }

  def toApi(findModelResponse: FindModelResponse): ListModelsResponse = {
    ListModelsResponse(findModelResponse.models.map(toApi), toApi(findModelResponse.nextPageToken))
  }

  def toApi(money: Money): api.Money = {
    api.Money(money.currency.code, money.amount.toFloat)
  }

  def toApi(model: Model): api.Model = {
    api.Model(
      ModelName(model.categoryId, model.id).toNameString,
      model.id.show,
      model.readableId.show,
      model.displayName.show,
      model.description.value,
      Some(toApi(model.imageList)),
      Some(toApi(model.minimalPrice.value)),
    )
  }

  def toApi(imageList: ImageList): api.ImageList = {
    api.ImageList(
      ImageListName(imageList.id).toNameString,
      imageList.displayName.show,
      imageList.images.map(toApi)
    )
  }

  def toApi(image: Image): api.Image = {
    api.Image(
      image.src.show,
      image.alt.show
    )
  }

  def toApi(p: Product): api.Product = {
    ???
  }

  def toDomain(category: api.Category): Category = {
    Category(
      CategoryUUID(UUID.randomUUID()),
      CategoryReadableId(category.readableId),
      CategoryDisplayName(category.displayName),
      CategoryDescription(category.description)
    )
  }

  def toDomain(request: CreateCategoryRequest): CreateCategory = {
    val category = request.category.get
    CreateCategory(
      CategoryReadableId(category.readableId),
      CategoryDisplayName(category.displayName),
      CategoryDescription(category.description)
    )
  }

  def toDomain(request: GetProductRequest): ProductId = {
    ApiName.product(request.name).toOption.get.productId
  }

  def toDomain(request: DeleteModelRequest): ModelId = {
    ApiName.model(request.name).toOption.get.modelId
  }
  def toDomain(request: CreateModelRequest): CreateModel = {
    val models = ApiName.models(request.parent).toOption.get.categoryId
    val model = request.model.get
    val imageListId = ApiName.imageList(model.imageList.get.name).toOption.get.id

    CreateModel(
      ModelReadableId(model.readableId),
      models,
      ModelDisplayName(model.displayName),
      ModelDescription(model.description),
      imageListId,
      List.empty //TODO REPLCAE
    )
  }

  private def nonEmptyString(s: String): Option[String] = Option.when(s.isEmpty)(s)

  def toDomain(request: UpdateCategoryRequest): UpdateCategory = {
    val category1 = request.category.get
    val categoryId = ApiName.category(category1.name).map(_.categoryId).toOption.get
    val fieldMask = request.updateMask.get
    val category = FieldMaskUtil.applyFieldMask(category1, fieldMask)
    UpdateCategory(
      categoryId,
      nonEmptyString(category.readableId).map(CategoryReadableId.apply),
      nonEmptyString(category.displayName).map(CategoryDisplayName.apply),
      nonEmptyString(category.description).map(CategoryDescription.apply)
    )
  }
}
