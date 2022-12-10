package ua.pomo.catalog.shared

import cats.Applicative
import cats.syntax.apply._
import org.scalacheck.Gen
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.{category, image, imageList, model, product}
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.parameter._
import ua.pomo.common.domain.repository
import ua.pomo.common.domain.repository.Query

object Generators {
  implicit class ToLazyListOps[T](g: Gen[T]) {
    def toLazyList: LazyList[T] = LazyList.continually(g.sample.get)
  }

  implicit val genApp: Applicative[Gen] = new Applicative[Gen] {
    override def pure[A](x: A): Gen[A] = Gen.const(x)

    override def ap[A, B](ff: Gen[A => B])(fa: Gen[A]): Gen[B] = fa.flatMap(a => ff.map(f => f(a)))
  }

  object Category {
    val catId: Gen[CategoryUUID] = Gen.uuid.map(CategoryUUID.apply)
    private val readableId: Gen[CategoryReadableId] = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)
    private val selector: Gen[CategorySelector] = Gen.oneOf[CategorySelector](
      CategorySelector.All,
      readableId.flatMap(CategorySelector.RidIs.apply),
      catId.flatMap((c: CategoryUUID) => CategorySelector.UidIs(c))
    )

    val update: Gen[CategoryUUID => UpdateCategory] = for {
      a <- Gen.option(readableId)
      b <- Gen.option(displayName)
      c <- Gen.option(description)
    } yield (id: CategoryUUID) => UpdateCategory(id, a, b, c)

    val create: Gen[CreateCategory] =
      (Gen.const[Option[CategoryUUID]](None), readableId, displayName, description).mapN(CreateCategory.apply)
    val gen: Gen[Category] = (catId, readableId, displayName, description).mapN(category.Category.apply)

    val query: Gen[CategoryQuery] = {
      for {
        s <- selector
        p <- PageToken.nonEmpty
      } yield Query(s, p)
    }
  }

  object Image {
    val id = Gen.uuid.map(ImageId.apply)
    private val alt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val src = Gen.alphaNumStr.map(ImageSrc.apply)

    val gen: Gen[Image] = (id, src, alt).mapN(image.Image.apply)
    val create: Gen[CreateImageMetadata] = (Gen.const[Option[ImageId]](None), src, alt).mapN(CreateImageMetadata.apply)
    val createListOf5: Gen[List[CreateImageMetadata]] = Gen.listOfN(5, create)
    val selector: Gen[ImageSelector] = Gen.oneOf(
      Gen.const(ImageSelector.All),
      id.map(ImageSelector.IdIs.apply)
    )
    val query: Gen[ImageQuery] = for {
      s <- selector
      p <- PageToken.nonEmpty
    } yield Query(s, p)

    def update: Gen[ImageId => BuzzImageUpdate] = Gen.const(id => BuzzImageUpdate(id))
  }

  object ImageList {
    private val listId = Gen.uuid.map(ImageListId.apply)
    private val imageId = Gen.uuid.map(ImageId.apply)
    private val imageSrc = Gen.alphaNumStr.map(ImageSrc.apply)
    private val imageAlt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val displayName = Gen.alphaNumStr.map(ImageListDisplayName.apply)
    private[shared] val imageGen =
      (imageId, imageSrc, imageAlt).mapN(image.Image.apply)
    private val imageListGen = Gen.listOf(imageGen).map(_.groupBy(_.src).values.map(_.head).toList)

    def update(listGen: Gen[List[ImageId]] = Gen.listOf(imageId)): Gen[imageList.UpdateImageList] = (
      listId,
      Gen.option(displayName),
      Gen.option(listGen)
    ).mapN(imageList.UpdateImageList.apply)

    def gen(genImages: Gen[List[Image]] = imageListGen): Gen[imageList.ImageList] =
      (listId, displayName, genImages).mapN(imageList.ImageList.apply)

    def genCreate(genImages: Gen[List[ImageId]]): Gen[imageList.CreateImageList] =
      (listId.map(Some(_)), displayName, genImages).mapN(imageList.CreateImageList.apply)
  }

  object Model {
    private val id = Gen.const(ModelId(Gen.uuid.sample.get))
    private val catId = Gen.const(CategoryUUID(Gen.uuid.sample.get))
    private val catRid = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val rId = Gen.alphaNumStr.map(ModelReadableId.apply)
    private val rDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val rDescription = Gen.alphaNumStr.map(ModelDescription.apply)
    private val parameterId = Gen.uuid.map(ParameterId.apply)
    private val parameterDisplayName = Gen.alphaNumStr.map(ParameterDisplayName.apply)
    private val param = (parameterId, parameterDisplayName, Gen.option(ImageList.imageGen)).mapN(Parameter.apply)
    private val paramListId = Gen.uuid.map(ParameterListId.apply)
    private val paramListDisplayName = Gen.alphaNumStr.map(ParamListDisplayName.apply)
    private val params = Gen.listOfN(2, param)
    private val paramList = (paramListId, paramListDisplayName, params).mapN(ParameterList.apply)
    private val paramLists = Gen.listOfN(2, paramList)
    private val rMoney = Gen.posNum[Double].map(Money(_, USD)).map(ModelMinimalPrice.apply)

    def createGen(imListId: ImageListId, parameterListIds: List[ParameterListId]): Gen[CreateModel] =
      (Gen.const(None), rId, catId, rDisplayName, rDescription, Gen.const(imListId), Gen.const(parameterListIds))
        .mapN(model.CreateModel.apply)

    val gen: Gen[model.Model] =
      (id, rId, catId, catRid, rDisplayName, rDescription, rMoney, paramLists, ImageList.gen()).mapN(model.Model.apply)

    def updateGen(imageListId: ImageListId, categoryId: CategoryUUID): Gen[model.UpdateModel] =
      (
        id,
        Gen.option(rId),
        Gen.option(Gen.const(categoryId)),
        Gen.option(rDisplayName),
        Gen.option(rDescription),
        Gen.option(Gen.const(imageListId))
      )
        .mapN(model.UpdateModel.apply)
  }

  object PageToken {
    val nonEmpty = (Gen.posNum[Long], Gen.posNum[Long]).mapN(repository.PageToken.NonEmpty.apply)
    val gen: Gen[repository.PageToken] = Gen.oneOf(Gen.const(repository.PageToken.Empty), nonEmpty)
  }

  object Product {
    private val id = Gen.uuid.map(ProductId.apply)
    private val modelId = Gen.uuid.map(ModelId.apply)
    private val displayName = Gen.alphaNumStr.map(ProductDisplayName.apply)
    private val categoryId = Gen.uuid.map(CategoryUUID.apply)
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

    private val imageListId = Gen.uuid.map(ImageListId.apply)
    def create(imageListId1: ImageListId, modelId1: ModelId, paramIds: List[ParameterId]): Gen[CreateProduct] =
      (
        Gen.const[Option[ProductId]](None),
        Gen.const(modelId1),
        Gen.const(imageListId1),
        standardPrice,
        promoPrice,
        Gen.const(paramIds)
      )
        .mapN(CreateProduct.apply)

    val update: Gen[UpdateProduct] =
      (id, Gen.option(imageListId), Gen.option(standardPrice), Gen.option(promoPrice))
        .mapN(UpdateProduct.apply)
        .filter(
          _.productIterator
            .collect { case x: Option[_] =>
              x
            }
            .exists(_.isDefined)
        )
  }
}
