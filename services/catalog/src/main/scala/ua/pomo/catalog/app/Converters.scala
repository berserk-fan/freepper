package ua.pomo.catalog.app

import cats.implicits.toShow
import io.circe.{Decoder, Encoder, parser}
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{CreateCategoryRequest, CreateModelRequest, ListModelsRequest, ListModelsResponse}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import scala.util.{Success, Try}

object Converters {
  def toApi(cat: Category): api.Category = {
    api.Category(
      CategoryName(cat.uuid).toNameString,
      cat.uuid.value.toString,
      cat.displayName.value,
      cat.description.value
    )
  }

  private val charset = StandardCharsets.UTF_8
  case class PageTokenStr(value: String)
  def toApi(pageToken: PageToken): String = {
    Base64.getEncoder.encodeToString(Encoder[PageToken].apply(pageToken).show.getBytes(charset))
  }
  def toDomain(pageToken: PageTokenStr): Try[PageToken] = {
    Try(new String(Base64.getDecoder.decode(pageToken.value), charset))
      .flatMap(parser.parse(_).toTry)
      .flatMap(Decoder[PageToken].decodeJson(_).toTry)
  }

  //TODO consider removing logic of pagetoken from converter
  def toDomain(listModels: ListModelsRequest): Try[FindModel] = {
    val categoryId = ApiName.models(listModels.parent).toOption.get.categoryId.get
    val pageToken = listModels.pageToken match {
      case "" => Success(PageToken.NotEmpty(listModels.pageSize.toLong, 0L))
      case s => toDomain(PageTokenStr(s))
    }
    pageToken.map(FindModel(categoryId, _))
  }

  def toApi(findModelResponse: FindModelResponse): ListModelsResponse = {
    ListModelsResponse(findModelResponse.models.map(toApi), toApi(findModelResponse.nextPageToken))
  }

  def toApi(model: Model): api.Model = {
    api.Model(
      ModelName(None, model.id).toNameString,
      model.id.show,
      model.readableId.show,
      model.displayName.show,
      model.description.value,
      model.minimalPrice.value.amount.toInt,
      Some(toApi(model.imageList))
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

  def toDomain(request: CreateCategoryRequest): Category = {
    val category = request.category.get
    Category(
      CategoryUUID(UUID.randomUUID()),
      CategoryReadableId(category.readableId),
      CategoryDisplayName(category.displayName),
      CategoryDescription(category.description)
    )
  }

  def toDomain(request: CreateModelRequest): CreateModel = {
    val models = ApiName.models(request.parent).toOption.flatMap(_.categoryId).get
    val model = request.model.get
    val imageListId = ApiName.imageList(model.imageList.get.name).toOption.get.id

    CreateModel(
      ModelReadableId(model.readableId),
      models,
      ModelDisplayName(model.displayName),
      ModelDescription(model.description),
      imageListId
    )
  }
}
