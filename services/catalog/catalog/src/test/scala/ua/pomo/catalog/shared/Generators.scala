package ua.pomo.catalog.shared

import cats.Applicative
import cats.data.NonEmptyList
import cats.syntax.apply._
import org.scalacheck.Gen
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.{category, image, imageList, model, parameter, product}
import ua.pomo.common.domain.crud
import ua.pomo.common.domain.crud.Query

object Generators {
  implicit class ToLazyListOps[T](g: Gen[T]) {
    def toLazyList: LazyList[T] = LazyList.continually(g.sample.get)
  }

  implicit val genApp: Applicative[Gen] = new Applicative[Gen] {
    override def pure[A](x: A): Gen[A] = Gen.const(x)

    override def ap[A, B](ff: Gen[A => B])(fa: Gen[A]): Gen[B] = fa.flatMap(a => ff.map(f => f(a)))
  }

  object Category {
    val catId: Gen[CategoryId] = Gen.uuid.map(CategoryId.apply)
    private val readableId: Gen[CategoryReadableId] = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)
    private val selector: Gen[CategorySelector] = Gen.oneOf[CategorySelector](
      CategorySelector.All,
      readableId.flatMap(CategorySelector.RidIs.apply),
      catId.flatMap((c: CategoryId) => CategorySelector.UidIs(c))
    )

    val update: Gen[CategoryId => UpdateCategory] = for {
      a <- Gen.option(readableId)
      b <- Gen.option(displayName)
      c <- Gen.option(description)
    } yield (id: CategoryId) => UpdateCategory(id, a, b, c)

    val create: Gen[CreateCategory] =
      (catId, readableId, displayName, description).mapN(CreateCategory.apply)
    val gen: Gen[Category] = (catId, readableId, displayName, description).mapN(category.Category.apply)

    val query: Gen[CategoryQuery] = Query.gen(selector)
  }

  object Image {
    val id: Gen[ImageId] = Gen.uuid.map(ImageId.apply)
    private val alt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val src = Gen.alphaNumStr.map(ImageSrc.apply)
    private val imageData = Gen.const(Array[Byte]()).map(ImageData.apply)

    val gen: Gen[Image] = (id, src, alt).mapN(image.Image.apply)
    val create: Gen[CreateImage] = (id, src, alt, imageData).mapN(CreateImage.apply)
    val createListOf5: Gen[List[CreateImage]] = Gen.listOfN(5, create)
    val selector: Gen[ImageSelector] = Gen.oneOf(
      Gen.const(ImageSelector.All),
      id.map(ImageSelector.IdIs.apply)
    )
    val query: Gen[ImageQuery] = Query.gen(selector)

    def update: Gen[ImageId => BuzzImageUpdate] = Gen.const(id => BuzzImageUpdate(id))
  }

  object ImageList {
    val id: Gen[ImageListId] = Gen.uuid.map(ImageListId.apply)
    private val imageId = Gen.uuid.map(ImageId.apply)
    private val imageSrc = Gen.alphaNumStr.map(ImageSrc.apply)
    private val imageAlt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val displayName = Gen.alphaNumStr.map(ImageListDisplayName.apply)
    private[shared] val imageGen =
      (imageId, imageSrc, imageAlt).mapN(image.Image.apply)
    private val imageListGen = Gen.listOf(imageGen).map(_.groupBy(_.src).values.map(_.head).toList)

    def update(listGen: Gen[List[ImageId]] = Gen.listOf(imageId)): Gen[ImageListId => imageList.UpdateImageList] = for {
      a <- Gen.option(displayName)
      b <- Gen.option(listGen)
    } yield (id: ImageListId) => imageList.UpdateImageList(id, a, b)

    def gen(genImages: Gen[List[Image]] = imageListGen): Gen[imageList.ImageList] =
      (id, displayName, genImages).mapN(imageList.ImageList.apply)

    def genCreate(genImages: Gen[List[ImageId]]): Gen[imageList.CreateImageList] =
      (id, displayName, genImages).mapN(imageList.CreateImageList.apply)

    val selector: Gen[ImageListSelector] = Gen.oneOf(
      Gen.const(ImageListSelector.All),
      Gen.nonEmptyListOf(id).map(NonEmptyList.fromListUnsafe(_)).map(ImageListSelector.IdsIn(_))
    )

    val query: Gen[ImageListQuery] = Query.gen(selector)
  }

  object ParameterList {
    private val parameterId = Gen.uuid.map(ParameterId.apply)
    private val parameterDisplayName = Gen.alphaNumStr.map(ParameterDisplayName.apply)
    private val parameterDescription = Gen.alphaNumStr.map(ParameterDescription.apply)
    private val param: Gen[Parameter] =
      (parameterId, parameterDisplayName, Gen.option(Image.gen), Gen.option(parameterDescription)).mapN(Parameter.apply)

    private def createParameter(imageId: Gen[ImageId]): Gen[CreateParameter] =
      (parameterId, parameterDisplayName, Gen.option(imageId), Gen.option(parameterDescription))
        .mapN(CreateParameter.apply)

    val paramListId: Gen[ParameterListId] = Gen.uuid.map(ParameterListId.apply)
    private val paramListDisplayName = Gen.alphaNumStr.map(ParamListDisplayName.apply)

    val paramList: Gen[ParameterList] =
      (paramListId, paramListDisplayName, Gen.listOf(param)).mapN(parameter.ParameterList.apply)
    def create(imageIdGen: Gen[ImageId]): Gen[CreateParameterList] = for {
      imageId <- imageIdGen
      id <- paramListId
      dn <- paramListDisplayName
      params <- Gen.listOf(createParameter(imageId)).filter(_.nonEmpty)
    } yield CreateParameterList(id, dn, params)

    def update(imageIdGen: Gen[ImageId]): Gen[ParameterListId => UpdateParameterList] = for {
      dn <- Gen.option(paramListDisplayName)
      params <- Gen.option(Gen.listOf(createParameter(imageIdGen)).filter(_.nonEmpty))
    } yield id => UpdateParameterList(id, dn, params)

    private val selector: Gen[ParameterListSelector] = Gen.oneOf(
      Gen.const[ParameterListSelector](ParameterListSelector.All),
      paramListId.map(ParameterListSelector.IdIs)
    )

    val query: Gen[Query[ParameterListSelector]] = Query.gen(selector)

    val paramLists: Gen[List[ParameterList]] = Gen.listOf(paramList)
  }

  object Model {
    val id: Gen[ModelId] = Gen.const(ModelId(Gen.uuid.sample.get))
    private val catId = Gen.const(CategoryId(Gen.uuid.sample.get))
    private val catRid = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val rId = Gen.alphaNumStr.map(ModelReadableId.apply)
    private val rDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val rDescription = Gen.alphaNumStr.map(ModelDescription.apply)
    private val rMoney = Gen.posNum[Double].map(Money(_, USD)).map(ModelMinimalPrice.apply)

    def createGen(
        imListId: ImageListId,
        parameterListIds: List[ParameterListId],
        catId: Gen[CategoryId],
        idOpt: Option[ModelId] = None
    ): Gen[CreateModel] =
      (
        idOpt.fold(id)(Gen.const),
        rId,
        catId,
        rDisplayName,
        rDescription,
        Gen.const(imListId),
        Gen.const(parameterListIds)
      )
        .mapN(model.CreateModel.apply)

    val gen: Gen[model.Model] =
      (id, rId, catId, catRid, rDisplayName, rDescription, rMoney, ParameterList.paramLists, ImageList.gen())
        .mapN(model.Model.apply)

    private val selector: Gen[ModelSelector] = Gen.oneOf(
      Gen.const(ModelSelector.All),
      id.map(ModelSelector.IdIs),
      catId.map(ModelSelector.CategoryIdIs),
      rId.map(ModelSelector.RidIs)
    )

    val query: Gen[Query[ModelSelector]] = Query.gen(selector)

    def updateGen(imageListId: ImageListId, categoryId: CategoryId): Gen[ModelId => model.UpdateModel] =
      (
        Gen.const(id.sample.get),
        Gen.option(rId),
        Gen.option(Gen.const(categoryId)),
        Gen.option(rDisplayName),
        Gen.option(rDescription),
        Gen.option(Gen.const(imageListId))
      )
        .mapN(model.UpdateModel.apply)
        .map(um => (m: ModelId) => um.copy(id = m))
  }

  object PageToken {
    val nonEmpty: Gen[crud.PageToken.NonEmpty] =
      (Gen.posNum[Long], Gen.posNum[Long]).mapN(crud.PageToken.NonEmpty.apply)
    val gen: Gen[crud.PageToken] = Gen.oneOf(Gen.const(crud.PageToken.Empty), nonEmpty)
  }

  object Query {
    def gen[T](s: Gen[T]): Gen[crud.Query[T]] = {
      for {
        si <- s
        p <- PageToken.nonEmpty
      } yield crud.Query(si, p)
    }
  }

  object Product {
    val id: Gen[ProductId] = Gen.uuid.map(ProductId.apply)
    private val modelId = Gen.uuid.map(ModelId.apply)
    private val displayName = Gen.alphaNumStr.map(ProductDisplayName.apply)
    private val categoryId = Gen.uuid.map(CategoryId.apply)
    private val parameterId = Gen.uuid.map(ParameterId.apply)
    private val paramIds = Gen.listOfN(2, parameterId)
    private val standardPrice: Gen[ProductStandardPrice] = Gen.posNum[Double].map(ProductStandardPrice.apply)
    private val promoPrice: Gen[Option[ProductPromoPrice]] = Gen.option(Gen.posNum[Double].map(ProductPromoPrice.apply))

    private val price =
      (standardPrice, promoPrice)
        .mapN(ProductPrice.apply)
    val gen: Gen[product.Product] =
      (id, modelId, displayName, categoryId, ImageList.gen(), price, paramIds)
        .mapN(product.Product.apply)

    private val s: Gen[ProductSelector] = Gen.oneOf(
      Gen.const(ProductSelector.All),
      id.map(ProductSelector.IdIs),
      Gen.nonEmptyListOf(id).map(NonEmptyList.fromListUnsafe).map(ProductSelector.IdIn),
      modelId.map(ProductSelector.ModelIs)
    )

    val query: Gen[Query[ProductSelector]] = Query.gen(s)

    def create(imageListId1: ImageListId, modelId1: ModelId, paramIds: List[ParameterId]): Gen[CreateProduct] =
      (
        id,
        Gen.const(modelId1),
        Gen.const(imageListId1),
        standardPrice,
        promoPrice,
        Gen.const(paramIds)
      )
        .mapN(CreateProduct.apply)

    def update(imageListId1: ImageListId): Gen[ProductId => UpdateProduct] =
      (Gen.const(id.sample.get), Gen.option(imageListId1), Gen.option(standardPrice), Gen.option(promoPrice))
        .mapN(UpdateProduct.apply)
        .map((x: UpdateProduct) => (id: ProductId) => x.copy(id = id))
  }
}
