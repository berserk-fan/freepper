package ua.pomo.catalog.app

import cats.implicits.toShow
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import io.circe.{Decoder, Encoder, parser}
import scalapb.{FieldMaskUtil, GeneratedMessage, GeneratedMessageCompanion}
import squants.market.Money
import ua.pomo.catalog.api
import ua.pomo.catalog.api.{
  CreateCategoryRequest,
  CreateImageListRequest,
  CreateImageRequest,
  CreateModelRequest,
  CreateProductRequest,
  DeleteCategoryRequest,
  DeleteImageListRequest,
  DeleteImageRequest,
  DeleteModelRequest,
  DeleteProductRequest,
  GetCategoryRequest,
  GetImageListRequest,
  GetImageRequest,
  GetModelRequest,
  GetProductRequest,
  ListCategoriesRequest,
  ListCategoriesResponse,
  ListImageListsRequest,
  ListImageListsResponse,
  ListImagesRequest,
  ListImagesResponse,
  ListModelsRequest,
  ListModelsResponse,
  ListProductsRequest,
  ListProductsResponse,
  UpdateCategoryRequest,
  UpdateImageListRequest,
  UpdateModelRequest
}
import ua.pomo.catalog.app.ApiName._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.error.ValidationErr
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.image._

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}
import scala.util.{Failure, Try}

object Converters {
  private val DefaultUUID = new UUID(0, 0)
  def toApi(cat: Category): api.Category = {
    api.Category(
      CategoryName(CategoryRefId.Readable(cat.readableId)).toNameString,
      cat.id.value.toString,
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

  private def parseToken(pageToken: String, pageSize: Int): PageToken.NonEmpty = {
    val pageTokenDecoded = new String(Base64.getDecoder.decode(pageToken), utf8)
    pageTokenDecoded match {
      case "" => PageToken.NonEmpty(pageSize.toLong, 0L)
      case s =>
        val x = parser.parse(s).toTry.get
        Decoder[PageToken.NonEmpty].decodeJson(x).toTry.get
    }
  }

  def toDomain(listModels: ListModelsRequest): ModelQuery = {
    val categoryId = ApiName.models(listModels.parent).toTry.get.categoryId.uid
    ModelQuery(ModelSelector.CategoryIdIs(categoryId), parseToken(listModels.pageToken, listModels.pageSize))
  }

  def toDomain(listImages: ListImageListsRequest): ImageListQuery = {
    ApiName.imageLists(listImages.parent).toTry.get
    ImageListQuery(ImageListSelector.All, parseToken(listImages.pageToken, listImages.pageSize))
  }

  def toApi(listImages: FindImageListResponse): ListImageListsResponse = {
    ListImageListsResponse(listImages.imageLists.map(toApi), toApi(listImages.nextPageToken))
  }

  def toApi(findModelResponse: FindModelResponse): ListModelsResponse = {
    ListModelsResponse(findModelResponse.models.map(toApi), toApi(findModelResponse.nextPageToken))
  }

  def toApi(products: FindProductResponse): ListProductsResponse = {
    ListProductsResponse(products.products.map(toApi), toApi(products.nextPageToken))
  }

  def toApi(resp: FindImagesResponse): ListImagesResponse = {
    ListImagesResponse(resp.images.map(toApi), toApi(resp.nextPageToken))
  }

  def toApi(money: Money): api.Money = {
    api.Money(money.amount.toFloat)
  }

  def toApi(parameter: Parameter): api.Parameter = {
    api.Parameter(parameter.id.show, parameter.displayName.show, parameter.image.map(toApi))
  }

  def toApi(parameterList: ParameterList): api.ParameterList = {
    val parameters = parameterList.parameters.map(toApi)
    api.ParameterList(parameterList.id.show, parameterList.displayName.show, parameters)
  }

  def toApi(resp: QueryCategoriesResponse): ListCategoriesResponse = {
    ListCategoriesResponse(resp.categories.map(toApi), toApi(resp.nextToken))
  }
  def toApi(model: Model): api.Model = {
    api.Model(
      ModelName(CategoryRefId.Readable(model.categoryRid), model.id).toNameString,
      model.id.show,
      model.readableId.show,
      model.displayName.show,
      model.description.value,
      Some(toApi(model.imageList)),
      Some(toApi(model.minimalPrice.value)),
      model.parameterLists.map(toApi).toList
    )
  }

  def toApi(imageList: ImageList): api.ImageList = {
    api.ImageList(
      ImageListName(imageList.id).toNameString,
      imageList.id.show,
      imageList.displayName.show,
      imageList.images.map(toApi)
    )
  }

  def toApi(p: Product): api.Product = {
    api.Product(
      ProductName(CategoryRefId.Uid(p.categoryId), p.modelId, p.id).toNameString,
      p.id.show,
      p.displayName.show,
      p.modelId.show,
      Some(toApi(p.imageList)),
      Some(api.Product.Price(Some(api.Money(p.price.standard.value.toFloat)))),
      p.parameterIds.map(_.show)
    )
  }

  def toDomain(category: api.Category): Category = {
    Category(
      CategoryUUID(UUID.randomUUID()),
      CategoryReadableId(category.readableId),
      CategoryDisplayName(category.displayName),
      CategoryDescription(category.description)
    )
  }

  def toDomain(request: GetImageRequest): ImageId = {
    ApiName.image(request.name).toTry.get.id
  }

  def toDomain(request: GetCategoryRequest): CategoryUUID = {
    ApiName.category(request.name).toTry.get.categoryId.uid
  }

  def toDomain(request: DeleteImageRequest): ImageId = {
    ApiName.image(request.name).toTry.get.id
  }

  def toDomain(request: DeleteCategoryRequest): CategoryUUID = {
    ApiName.category(request.name).toTry.get.categoryId.uid
  }

  def toDomain(request: GetModelRequest): ModelId = {
    ApiName.model(request.name).toTry.get.modelId
  }

  def toDomain(request: CreateCategoryRequest): CreateCategory = {
    val category = request.category.get
    CreateCategory(
      CategoryReadableId(category.readableId),
      CategoryDisplayName(category.displayName),
      CategoryDescription(category.description)
    )
  }

  def toDomain(req: ListCategoriesRequest): CategoryQuery = {
    val token = parseToken(req.pageToken, req.pageSize)
    CategoryQuery(CategorySelector.All, token)
  }

  def toDomain(request: UpdateImageListRequest): ImageListUpdate = {
    val id = ApiName.imageList(request.imageList.get.name).toTry.get.id
    val obj = applyFieldMask(request.imageList.get, request.updateMask.get)
    ImageListUpdate(
      id,
      nonEmptyString(obj.displayName).map(ImageListDisplayName.apply),
      nonEmptyList(obj.images.toList.map(image => ApiName.image(image.name).toTry.get.id))
    )
  }

  def toDomain(request: ListProductsRequest): ProductQuery = {
    val modelId = ApiName.products(request.parent).toTry.get.modelId
    ProductQuery(parseToken(request.pageToken, request.pageSize), ProductSelector.ModelIs(modelId))
  }

  def toDomain(request: ListImagesRequest): ImageQuery = {
    ImageQuery(ImageSelector.All, parseToken(request.pageToken, request.pageSize))
  }

  def toDomain(request: UpdateModelRequest): UpdateModel = {
    val model = request.model.get
    val modelName = ApiName.model(model.name).toTry.get
    val model2 = applyFieldMask(model, request.updateMask.get)
    val imageListId = model2.imageList.map(_.name).map(ApiName.imageList).map(_.toTry.get.id)
    UpdateModel(
      modelName.modelId,
      nonEmptyString(model2.readableId).map(ModelReadableId.apply),
      None,
      nonEmptyString(model2.displayName).map(ModelDisplayName.apply),
      nonEmptyString(model2.description).map(ModelDescription.apply),
      imageListId
    )
  }

  def toDomain(request: GetProductRequest): ProductId = {
    ApiName.product(request.name).toTry.get.productId
  }

  def toDomain(request: CreateProductRequest): CreateProduct = {
    val productsName = ApiName.products(request.parent).toTry.get
    val product = request.product.get
    val imageListId = Try(UUID.fromString(product.imageList.get.uid))
      .recoverWith { case e: Throwable => Failure(ValidationErr(s"bad imageList id $product.imageList", Some(e))) }
      .map(ImageListId.apply)
      .get

    val res = CreateProduct(
      productsName.modelId,
      imageListId,
      ProductStandardPrice(product.price.get.standard.get.amount.toDouble),
      None,
      product.parameterIds.map(UUID.fromString).map(ParameterId.apply).toList
    )

    res
  }

  def toDomain(request: CreateImageListRequest): ImageList = {
    val il = request.imageList.get
    ImageList(
      ImageListId(DefaultUUID),
      ImageListDisplayName(il.displayName),
      il.images.map(im => Image(ApiName.image(im.name).toTry.get.id, ImageSrc(im.src), ImageAlt(im.alt))).toList
    )
  }

  def toDomain(request: DeleteProductRequest): ProductId = {
    ApiName.product(request.name).toTry.get.productId
  }

  def toDomain(request: DeleteModelRequest): ModelId = {
    ApiName.model(request.name).toTry.get.modelId
  }

  def toDomain(request: CreateModelRequest): CreateModel = {
    val models = ApiName.models(request.parent).toTry.get.categoryId.uid
    val model = request.model.get
    val imageListId = ApiName.imageList(model.imageList.get.name).toTry.get.id

    CreateModel(
      ModelReadableId(model.readableId),
      models,
      ModelDisplayName(model.displayName),
      ModelDescription(model.description),
      imageListId,
      model.parameterLists.map(_.uid).map(UUID.fromString).map(ParameterListId.apply).toList
    )
  }

  private def nonEmptyString(s: String): Option[String] = Option.when(s.nonEmpty)(s)
  private def nonEmptyList[T](l: List[T]): Option[List[T]] = Option.when(l.nonEmpty)(l)

  def applyFieldMask[M <: GeneratedMessage: GeneratedMessageCompanion](m: M, fieldMask: FieldMask): M = {
    if (fieldMask.paths == Seq("*")) {
      m
    } else {
      FieldMaskUtil.applyFieldMask(m, fieldMask)
    }
  }

  def toDomain(req: CreateImageRequest): CreateImage = {
    val im = req.image.get
    CreateImage(ImageSrc(im.src), ImageAlt(im.alt), ImageData(im.data.toByteArray))
  }

  def toApi(image: Image): api.Image = {
    api.Image(
      ApiName.ImageName(image.id).toNameString,
      image.id.show,
      image.src.show,
      image.alt.show,
      ByteString.EMPTY
    )
  }

  def toDomain(request: UpdateCategoryRequest): UpdateCategory = {
    val category1 = request.category.get
    val categoryId = ApiName.category(category1.name).map(_.categoryId).toTry.get.uid
    val fieldMask = request.updateMask.get
    val category = applyFieldMask(category1, fieldMask)
    UpdateCategory(
      categoryId,
      nonEmptyString(category.readableId).map(CategoryReadableId.apply),
      nonEmptyString(category.displayName).map(CategoryDisplayName.apply),
      nonEmptyString(category.description).map(CategoryDescription.apply)
    )
  }
}
