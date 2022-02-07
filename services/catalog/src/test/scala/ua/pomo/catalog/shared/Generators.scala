package ua.pomo.catalog.shared

import cats.Applicative
import cats.syntax.apply._
import org.scalacheck.Gen
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.{category, image, model, product}
import ua.pomo.catalog.domain
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.domain.size._
import ua.pomo.catalog.domain.param._

object Generators {
  implicit class ToLazyListOps[T](g: Gen[T]) {
    def toLazyList: LazyList[T] = LazyList.continually(g.sample.get)
  }

  implicit val genApp: Applicative[Gen] = new Applicative[Gen] {
    override def pure[A](x: A): Gen[A] = Gen.const(x)

    override def ap[A, B](ff: Gen[A => B])(fa: Gen[A]): Gen[B] = fa.flatMap(a => ff.map(f => f(a)))
  }

  object Category {
    private val catId: Gen[CategoryUUID] = Gen.uuid.map(CategoryUUID.apply)
    private val readableId: Gen[CategoryReadableId] = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)

    val update: Gen[UpdateCategory] = (
      catId,
      Gen.option(readableId),
      Gen.option(displayName),
      Gen.option(description),
    ).mapN(UpdateCategory.apply)

    val create: Gen[CreateCategory] = (readableId, displayName, description).mapN(CreateCategory.apply)
    val gen: Gen[Category] = (catId, readableId, displayName, description).mapN(category.Category.apply)
  }

  object ImageList {
    private val listId = Gen.uuid.map(ImageListId.apply)
    private val imageId = Gen.uuid.map(ImageId.apply)
    private val imageSrc = Gen.alphaNumStr.map(ImageSrc.apply)
    private val imageAlt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val displayName = Gen.alphaNumStr.map(ImageListDisplayName.apply)
    private[shared] val imageGen = (imageId, imageSrc, imageAlt).mapN(Image.apply)
    private val imageListGen = Gen.listOf(imageGen).map(_.groupBy(_.src).values.map(_.head).toList)

    val update: Gen[image.ImageListUpdate] = (
      listId,
      Gen.option(displayName),
      Gen.option(imageListGen)
    ).mapN(image.ImageListUpdate.apply)

    val gen: Gen[image.ImageList] = (listId, displayName, imageListGen).mapN(image.ImageList.apply)
  }

  object Model {
    private val id = Gen.const(ModelId(Gen.uuid.sample.get))
    private val catId = Gen.const(CategoryUUID(Gen.uuid.sample.get))
    private val rId = Gen.alphaNumStr.map(ModelReadableId.apply)
    private val rDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val rDescription = Gen.alphaNumStr.map(ModelDescription.apply)
    private val rMoney = Gen.posNum[Double].map(Money(_, USD)).map(ModelMinimalPrice.apply)

    def createGen(imListId: ImageListId): Gen[CreateModel] =
      (rId, catId, rDisplayName, rDescription, Gen.const(imListId)).mapN(model.CreateModel.apply)

    val gen: Gen[model.Model] =
      (id, rId, catId, rDisplayName, rDescription, rMoney, ImageList.gen).mapN(model.Model.apply)

    def updateGen(imageListId: ImageListId, categoryId: CategoryUUID): Gen[model.UpdateModel] =
      (id,
       Gen.option(rId),
       Gen.option(Gen.const(categoryId)),
       Gen.option(rDisplayName),
       Gen.option(rDescription),
       Gen.option(Gen.const(imageListId)))
        .mapN(model.UpdateModel.apply)
  }

  object PageToken {
    private val nonEmpty = (Gen.posNum[Long], Gen.posNum[Long]).mapN(domain.PageToken.NonEmpty.apply)
    val gen: Gen[domain.PageToken] = Gen.oneOf(Gen.const(domain.PageToken.Empty), nonEmpty)
  }

  object Product {
    private val id = Gen.uuid.map(ProductId.apply)
    private val modelId = Gen.uuid.map(ModelId.apply)
    private val categoryId = Gen.uuid.map(CategoryUUID.apply)
    private val modelDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val fabricId = Gen.uuid.map(FabricUUID.apply)
    private val fabricDisplayName = Gen.alphaNumStr.map(FabricDisplayName.apply)
    private val fabric = (fabricId, fabricDisplayName, ImageList.imageGen).mapN(ProductFabric.apply)
    private val sizeId = Gen.uuid.map(SizeUUID.apply)
    private val sizeDisplayName = Gen.alphaNumStr.map(SizeDisplayName.apply)
    private val size = (sizeId, sizeDisplayName).mapN(ProductSize.apply)
    private val displayName =
      (modelDisplayName, fabricDisplayName, sizeDisplayName).mapN(product.Product.createDisplayName)
    private val standardPrice: Gen[ProductStandardPrice] = Gen.posNum[Double].map(ProductStandardPrice.apply)
    private val promoPrice: Gen[Option[ProductPromoPrice]] = Gen.option(Gen.posNum[Double].map(ProductPromoPrice.apply))
    private val price =
      (standardPrice, promoPrice)
        .mapN(ProductPrice.apply)
    val gen: Gen[product.Product] =
      (id, modelId, categoryId, displayName, fabric, size, ImageList.gen, price)
        .mapN(product.Product.apply)

    private val imageListId = Gen.uuid.map(ImageListId.apply)
    val createCommand: Gen[CreateProduct] = (id, modelId, imageListId, fabricId, sizeId, standardPrice, promoPrice)
      .mapN(CreateProduct.apply)

    val update: Gen[UpdateProduct] =
      (id,
       Gen.option(modelId),
       Gen.option(imageListId),
       Gen.option(fabricId),
       Gen.option(sizeId),
       Gen.option(standardPrice),
       Gen.option(promoPrice)).mapN(UpdateProduct.apply)
  }
}
