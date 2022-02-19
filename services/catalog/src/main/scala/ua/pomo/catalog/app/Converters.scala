package ua.pomo.catalog.app

import cats.implicits.toShow
import io.circe.{Decoder, Encoder, parser}
import scalapb.FieldMaskUtil
import squants.market.Money
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{
  CreateCategoryRequest,
  CreateImageListRequest,
  CreateModelRequest,
  DeleteImageListRequest,
  DeleteModelRequest,
  GetImageListRequest,
  GetProductRequest,
  ListModelsRequest,
  ListModelsResponse,
  UpdateCategoryRequest,
  UpdateImageListRequest
}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter.{Parameter, ParameterList, ParameterListId}
import ua.pomo.catalog.domain.product._

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import scala.util.{Success, Try}

object Converters {
  private val DefaultUUID = new UUID(0, 0)
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

  def toDomain(request: GetImageListRequest): ImageListId = {
    ApiName.imageList(request.name).toTry.get.id
  }

  def toDomain(deleteImageListRequest: DeleteImageListRequest): ImageListId = {
    ApiName.imageList(deleteImageListRequest.name).toTry.get.id
  }

  def toDomain(listModels: ListModelsRequest): Try[ModelQuery] = {
    val categoryId = ApiName.models(listModels.parent).toTry.get.categoryId
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
    api.Money(money.amount.toFloat)
  }

  def toApi(parameter: Parameter): api.Parameter = {
    api.Parameter(parameter.id.show, parameter.displayName.show, Some(toApi(parameter.image)))
  }

  def toApi(parameterList: ParameterList): api.ParameterList = {
    val parameters = parameterList.parameters.map(toApi)
    api.ParameterList(parameterList.id.show, parameterList.displayName.show, parameters)
  }

  def toApi(model: Model): api.Model = {
    api.Model(
      ModelName(model.categoryId, model.id).toNameString,
      model.id.show,
      model.readableId.show,
      model.displayName.show,
      model.description.value,
      api.Model.ImageList.ImageListData(toApi(model.imageList)),
      Some(toApi(model.minimalPrice.value)),
      api.Model.ParameterListsOneof.ParameterListsData(api.Model.ParameterLists(model.parameterLists.map(toApi)))
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

  def toDomain(image: api.Image): Image = {
    Image(ImageSrc(image.src), ImageAlt(image.alt))
  }

  def toDomain(request: UpdateImageListRequest): ImageListUpdate = {
    val id = ApiName.imageList(request.imageList.get.name).toTry.get.id
    val obj = FieldMaskUtil.applyFieldMask(request.imageList.get, request.updateMask.get)
    ImageListUpdate(id,
                    nonEmptyString(obj.displayName).map(ImageListDisplayName.apply),
                    nonEmptyList(obj.images.toList.map(Converters.toDomain)))
  }

  def toDomain(request: GetProductRequest): ProductId = {
    ApiName.product(request.name).toTry.get.productId
  }

  def toDomain(request: CreateImageListRequest): ImageList = {
    val il = request.imageList.get
    ImageList(
      ImageListId(DefaultUUID),
      ImageListDisplayName(il.displayName),
      il.images.map(im => Image(ImageSrc(im.src), ImageAlt(im.alt))).toList
    )
  }
  def toDomain(request: DeleteModelRequest): ModelId = {
    ApiName.model(request.name).toTry.get.modelId
  }
  def toDomain(request: CreateModelRequest): CreateModel = {
    val models = ApiName.models(request.parent).toTry.get.categoryId
    val model = request.model.get
    val imageListId = ApiName.imageList(model.imageList.imageListName.get).toTry.get.id

    CreateModel(
      ModelReadableId(model.readableId),
      models,
      ModelDisplayName(model.displayName),
      ModelDescription(model.description),
      imageListId,
      model.parameterLists.parameterListIds.get.value.map(UUID.fromString).map(ParameterListId.apply).toList
    )
  }

  private def nonEmptyString(s: String): Option[String] = Option.when(s.nonEmpty)(s)
  private def nonEmptyList[T](l: List[T]): Option[List[T]] = Option.when(l.nonEmpty)(l)

  def toDomain(request: UpdateCategoryRequest): UpdateCategory = {
    val category1 = request.category.get
    val categoryId = ApiName.category(category1.name).map(_.categoryId).toTry.get
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
