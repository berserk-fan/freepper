package com.freepper.catalog.app

import cats.{ApplicativeError, Monad, MonadError, MonadThrow, Traverse}
import cats.implicits.{catsSyntaxApplicativeError, toShow}
import com.google.protobuf.ByteString
import com.google.protobuf.field_mask.FieldMask
import io.circe.{Decoder, Encoder, parser}
import scalapb.{FieldMaskUtil, GeneratedMessage, GeneratedMessageCompanion}
import squants.market.Money
import com.freepper.catalog.api
import com.freepper.catalog.domain.product
import com.freepper.catalog.api.{
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
import com.freepper.catalog.app.ApiName._
import com.freepper.catalog.domain.category._
import com.freepper.catalog.domain.image._
import com.freepper.catalog.domain.imageList._
import com.freepper.catalog.domain.model._
import com.freepper.catalog.domain.parameter._
import com.freepper.catalog.domain.product._
import com.freepper.common.domain.crud.{ListResponse, PageToken, Query}
import com.freepper.common.domain.error.{ValidationErr, NotFound}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

import java.nio.charset.StandardCharsets
import java.util.{Base64, UUID}

class Converters[F[_]: MonadThrow](uuidGenerator: UUIDGenerator[F], idResolver: ReadableIdsResolver[F]) {
  private def fromStringOrRandomIfEmpty(s: String): F[UUID] = if (s == "") {
    uuidGenerator.gen
  } else {
    uuidGenerator.fromString(s)
  }

  def toApi(cat: Category): F[api.Category] = Monad[F].pure {
    api.Category(
      CategoryName(Right(cat.readableId)).toNameString,
      cat.id.value.toString,
      cat.readableId.value,
      cat.displayName.value,
      cat.description.value
    )
  }

  private val utf8 = StandardCharsets.UTF_8
  private def toApi(pageToken: PageToken): F[String] = Monad[F].pure {
    Base64.getEncoder.encodeToString {
      val res = pageToken match {
        case PageToken.Empty              => ""
        case x @ PageToken.NonEmpty(_, _) => Encoder[PageToken.NonEmpty].apply(x).show
      }
      res.getBytes(utf8)
    }
  }

  def toDomain(request: GetImageListRequest): F[ImageListId] = {
    MonadThrow[F].fromEither(ApiName.imageList(request.name)).map(_.id)
  }

  def toDomain(deleteImageListRequest: DeleteImageListRequest): F[ImageListId] = {
    MonadThrow[F].fromEither(ApiName.imageList(deleteImageListRequest.name)).map(_.id)
  }

  private def parseToken(pageToken: String, pageSize: Int): F[PageToken.NonEmpty] = for {
    pageTokenDecoded <- MonadThrow[F].catchNonFatal(new String(Base64.getDecoder.decode(pageToken), utf8))
    res <- pageTokenDecoded match {
      case "" => MonadThrow[F].pure(PageToken.NonEmpty(pageSize.toLong, 0L))
      case s =>
        for {
          parsed <- MonadThrow[F].fromEither(parser.parse(s))
          pageTokenJson <- MonadThrow[F].fromEither(Decoder[PageToken.NonEmpty].decodeJson(parsed))
        } yield pageTokenJson
    }
  } yield res

  def toDomain(listModels: ListModelsRequest): F[ModelQuery] = for {
    modelsName <- MonadThrow[F].fromEither(ApiName.models(listModels.parent))
    categoryId <- idResolver.resolveCategoryId(modelsName.categoryId)
    token <- parseToken(listModels.pageToken, listModels.pageSize)
  } yield Query(ModelSelector.CategoryIdIs(categoryId), token)

  def toDomain(listImages: ListImageListsRequest): F[ImageListQuery] = for {
    _ <- MonadThrow[F].fromEither(ApiName.imageLists(listImages.parent))
    token <- parseToken(listImages.pageToken, listImages.pageSize)
  } yield Query(ImageListSelector.All, token)

  def toApiListImageLists(listImageLists: ListResponse[ImageList]): F[ListImageListsResponse] = for {
    imageLists <- Traverse[List].traverse(listImageLists.entities)(toApi)
    token <- toApi(listImageLists.nextPageToken)
  } yield ListImageListsResponse(imageLists, token)

  def toApiListModels(findModelResponse: ListResponse[Model]): F[ListModelsResponse] = for {
    models <- Traverse[List].traverse(findModelResponse.entities)(toApi)
    token <- toApi(findModelResponse.nextPageToken)
  } yield ListModelsResponse(models, token)

  def toApiListProducts(products: ListResponse[product.Product]): F[ListProductsResponse] = for {
    productss <- Traverse[List].traverse(products.entities)(toApi)
    token <- toApi(products.nextPageToken)
  } yield ListProductsResponse(productss, token)

  def toApiListImages(resp: ListResponse[Image]): F[ListImagesResponse] = for {
    images <- Traverse[List].traverse(resp.entities)(toApi)
    token <- toApi(resp.nextPageToken)
  } yield ListImagesResponse(images, token)

  def toApi(money: Money): F[api.Money] = Monad[F].pure {
    api.Money(money.amount.toFloat)
  }

  def toApi(parameter: Parameter): F[api.Parameter] = for {
    images <- Traverse[Option].traverse(parameter.image)(toApi)
  } yield api.Parameter(parameter.id.show, parameter.displayName.show, images)

  def toApi(parameterList: ParameterList): F[api.ParameterList] = for {
    parameters <- Traverse[List].traverse(parameterList.parameters)(toApi)
  } yield api.ParameterList(parameterList.id.show, parameterList.displayName.show, parameters)

  def toApi(resp: ListResponse[Category]): F[ListCategoriesResponse] = for {
    x <- Traverse[List].traverse(resp.entities)(toApi)
    token <- toApi(resp.nextPageToken)
  } yield ListCategoriesResponse(x, token)

  def toApi(model: Model): F[api.Model] = for {
    imageList <- toApi(model.imageList)
    minPrice <- toApi(model.minimalPrice.value)
    parameterLists <- Traverse[List].traverse(model.parameterLists)(toApi)
  } yield api.Model(
    ModelName(Right(model.categoryRid), Left(model.id)).toNameString,
    model.id.show,
    model.readableId.show,
    model.displayName.show,
    model.description.value,
    Some(imageList),
    Some(minPrice),
    parameterLists
  )

  def toApi(imageList: ImageList): F[api.ImageList] = for {
    images <- Traverse[List].traverse(imageList.images)(toApi)
  } yield api.ImageList(
    ImageListName(imageList.id).toNameString,
    imageList.id.show,
    imageList.displayName.show,
    images
  )

  def toApi(p: product.Product): F[api.Product] = for {
    il <- toApi(p.imageList)
  } yield api.Product(
    ProductName(Left(p.categoryId), Left(p.modelId), p.id).toNameString,
    p.id.show,
    p.displayName.show,
    p.modelId.show,
    Some(il),
    Some(api.Product.Price(Some(api.Money(p.price.standard.value.toFloat)))),
    p.parameterIds.map(_.show)
  )

  def toDomain(category: api.Category): F[Category] = for {
    id <- uuidGenerator.gen
  } yield Category(
    CategoryId(id),
    CategoryReadableId(category.readableId),
    CategoryDisplayName(category.displayName),
    CategoryDescription(category.description)
  )

  def toDomain(request: GetImageRequest): F[ImageId] = {
    MonadThrow[F].fromEither(ApiName.image(request.name)).map(_.id)
  }

  def toDomain(request: GetCategoryRequest): F[CategoryId] = {
    MonadThrow[F]
      .fromEither(ApiName.category(request.name))
      .map(_.categoryId)
      .flatMap(idResolver.resolveCategoryId)
  }

  def toDomain(request: DeleteImageRequest): F[ImageId] = {
    MonadThrow[F].fromEither(ApiName.image(request.name)).map(_.id)
  }

  def toDomain(request: DeleteCategoryRequest): F[CategoryId] = {
    MonadThrow[F]
      .fromEither(ApiName.category(request.name))
      .map(_.categoryId)
      .flatMap(idResolver.resolveCategoryId)
  }

  def toDomain(request: GetModelRequest): F[ModelId] = {
    MonadThrow[F].fromEither(ApiName.model(request.name)).map(_.modelId).flatMap(idResolver.resolveModelId)
  }

  def toDomain(request: CreateCategoryRequest): F[CreateCategory] = for {
    category <- MonadThrow[F].fromOption(request.category, ValidationErr("category is missing"))
    id <- fromStringOrRandomIfEmpty(category.uid)
  } yield CreateCategory(
    CategoryId(id),
    CategoryReadableId(category.readableId),
    CategoryDisplayName(category.displayName),
    CategoryDescription(category.description)
  )

  def toDomain(req: ListCategoriesRequest): F[CategoryQuery] = for {
    token <- parseToken(req.pageToken, req.pageSize)
  } yield Query(CategorySelector.All, token)

  def toDomain(request: UpdateImageListRequest): F[UpdateImageList] = for {
    imageList <- MonadThrow[F].fromOption(
      request.imageList,
      ValidationErr("image list should be present on the request")
    )
    id <- MonadThrow[F].fromEither(ApiName.imageList(imageList.name)).map(_.id)
    obj = applyFieldMask(request.imageList.get, request.updateMask.get)
    images <- Traverse[List].traverse(obj.images.toList)(w => MonadThrow[F].fromEither(ApiName.image(w.name)))
    imageIds = images.map(_.id)
  } yield UpdateImageList(
    id,
    nonEmptyString(obj.displayName).map(ImageListDisplayName.apply),
    nonEmptyList(imageIds)
  )

  def toDomain(request: ListProductsRequest): F[ProductQuery] = for {
    modelId <- MonadThrow[F]
      .fromEither(ApiName.products(request.parent))
      .map(_.modelId)
      .flatMap(idResolver.resolveModelId)
    token <- parseToken(request.pageToken, request.pageSize)
  } yield Query(ProductSelector.ModelIs(modelId), token)

  def toDomain(request: ListImagesRequest): F[ImageQuery] = for {
    token <- parseToken(request.pageToken, request.pageSize)
  } yield Query(ImageSelector.All, token)

  def toDomain(request: UpdateModelRequest): F[UpdateModel] = for {
    model <- MonadThrow[F].fromOption(request.model, ValidationErr("not found model on the request"))
    modelName <- MonadThrow[F].fromEither(ApiName.model(model.name))
    updateMask <- MonadThrow[F].fromOption(request.updateMask, ValidationErr("not found field_mask on the request"))
    model2 = applyFieldMask(model, updateMask)
    imageListIdOpt <- Traverse[Option].traverse(model2.imageList)(imageList =>
      MonadThrow[F].fromEither(ApiName.imageList(imageList.name)).map(_.id)
    )
    modelIdResolved <- idResolver.resolveModelId(modelName.modelId)
  } yield UpdateModel(
    modelIdResolved,
    nonEmptyString(model2.readableId).map(ModelReadableId.apply),
    None,
    nonEmptyString(model2.displayName).map(ModelDisplayName.apply),
    nonEmptyString(model2.description).map(ModelDescription.apply),
    imageListIdOpt
  )

  def toDomain(request: GetProductRequest): F[ProductId] = {
    MonadThrow[F].fromEither(ApiName.product(request.name)).map(_.productId)
  }

  def toDomain(request: CreateProductRequest): F[CreateProduct] = for {
    productsName <- MonadThrow[F].fromEither(ApiName.products(request.parent))
    product <- MonadThrow[F].fromOption(request.product, ValidationErr("product should be present on the request"))
    imageListId <- uuidGenerator
      .fromString(product.imageList.get.uid)
      .recoverWith { case e: Throwable =>
        MonadThrow[F].raiseError(ValidationErr(s"bad imageList id $product.imageList", Some(e)))
      }
      .map(ImageListId.apply)
    id <- fromStringOrRandomIfEmpty(request.productId)
    parameterIdsRaw <- Traverse[List].traverse(product.parameterIds.toList)(uuidGenerator.fromString)
    parameterIds = parameterIdsRaw.map(ParameterId.apply)
    resolvedModelId <- idResolver.resolveModelId(productsName.modelId)
  } yield CreateProduct(
    ProductId(id),
    resolvedModelId,
    imageListId,
    ProductStandardPrice(product.price.get.standard.get.amount.toDouble),
    None,
    parameterIds
  )

  def toDomain(request: CreateImageListRequest): F[CreateImageList] = for {
    il <- MonadThrow[F].fromOption(request.imageList, ValidationErr("imageList is should be present on the request"))
    images <- Traverse[List].traverse(il.images.toList) { im =>
      MonadThrow[F].fromEither(ApiName.image(im.name)).map(_.id)
    }
    id <- fromStringOrRandomIfEmpty(request.imageListId)
  } yield CreateImageList(
    ImageListId(id),
    ImageListDisplayName(il.displayName),
    images
  )

  def toDomain(request: DeleteProductRequest): F[ProductId] = {
    MonadThrow[F].fromEither(ApiName.product(request.name)).map(_.productId)
  }

  def toDomain(request: DeleteModelRequest): F[ModelId] = {
    MonadThrow[F].fromEither(ApiName.model(request.name)).map(_.modelId).flatMap(idResolver.resolveModelId)
  }

  def toDomain(request: CreateModelRequest): F[CreateModel] = for {
    categoryId <- MonadThrow[F]
      .fromEither(ApiName.models(request.parent))
      .flatMap(name => idResolver.resolveCategoryId(name.categoryId))
    model <- MonadThrow[F].fromOption(request.model, ValidationErr("model should be present on the request"))
    imageListId <- MonadThrow[F].fromEither(ApiName.imageList(model.imageList.get.name)).map(_.id)
    id <- fromStringOrRandomIfEmpty(request.modelId)
    parameterListIds <- Traverse[List].traverse(model.parameterLists.map(_.uid).toList)(uidRaw =>
      uuidGenerator.fromString(uidRaw).map(ParameterListId.apply)
    )
  } yield CreateModel(
    ModelId(id),
    ModelReadableId(model.readableId),
    categoryId,
    ModelDisplayName(model.displayName),
    ModelDescription(model.description),
    imageListId,
    parameterListIds
  )

  private def nonEmptyString(s: String): Option[String] = Option.when(s.nonEmpty)(s)
  private def nonEmptyList[T](l: List[T]): Option[List[T]] = Option.when(l.nonEmpty)(l)

  def applyFieldMask[M <: GeneratedMessage: GeneratedMessageCompanion](m: M, fieldMask: FieldMask): M = {
    if (fieldMask.paths == Seq("*")) {
      m
    } else {
      FieldMaskUtil.applyFieldMask(m, fieldMask)
    }
  }

  def toDomain(req: CreateImageRequest): F[CreateImage] = for {
    im <- MonadThrow[F].fromOption(req.image, ValidationErr("image should be present on the request"))
    id <- fromStringOrRandomIfEmpty(req.imageId)
  } yield CreateImage(ImageId(id), ImageSrc(im.src), ImageAlt(im.alt), ImageData(im.data.toByteArray))

  def toApi(image: Image): F[api.Image] = Monad[F].pure {
    api.Image(
      ApiName.ImageName(image.id).toNameString,
      image.id.show,
      image.src.show,
      image.alt.show,
      ByteString.EMPTY
    )
  }

  def toDomain(request: UpdateCategoryRequest): F[UpdateCategory] = for {
    category1 <- MonadThrow[F].fromOption(request.category, ValidationErr("category should be present on the request"))
    categoryId <- MonadThrow[F]
      .fromEither(ApiName.category(category1.name).map(_.categoryId))
      .flatMap(idResolver.resolveCategoryId)
    fieldMask <- MonadThrow[F].fromOption(
      request.updateMask,
      ValidationErr("field_mask should be present on the request")
    )
    category = applyFieldMask(category1, fieldMask)
  } yield UpdateCategory(
    categoryId,
    nonEmptyString(category.readableId).map(CategoryReadableId.apply),
    nonEmptyString(category.displayName).map(CategoryDisplayName.apply),
    nonEmptyString(category.description).map(CategoryDescription.apply)
  )
}
