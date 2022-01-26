package ua.pomo.catalog.shared

import org.scalacheck.Gen
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.{category, image, model}
import ua.pomo.catalog.domain
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.image._

object Generators {
  implicit class ToLazyListOps[T](g: Gen[T]) {
    def toLazyList: LazyList[T] = LazyList.continually(g.sample.get)
  }

  object Category {
    private val catId: Gen[CategoryUUID] = Gen.uuid.map(CategoryUUID.apply)
    private val readableId: Gen[CategoryReadableId] = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)

    val update: Gen[UpdateCategory] = for {
      id <- catId
      readableId <- Gen.option(Generators.Category.readableId)
      displayName <- Gen.option(Generators.Category.displayName)
      descr <- Gen.option(Generators.Category.description)
    } yield UpdateCategory(id, readableId, displayName, descr)

    val self: Gen[Category] = for {
      id <- catId
      readableId <- readableId
      displayName <- displayName
      descr <- description
    } yield category.Category(id, readableId, displayName, descr)
  }

  object ImageList {
    private val listId = Gen.uuid.map(ImageListId.apply)
    private val imageId = Gen.uuid.map(ImageId.apply)
    private val imageSrc = Gen.alphaNumStr.map(ImageSrc.apply)
    private val imageAlt = Gen.alphaNumStr.map(ImageAlt.apply)
    private val displayName = Gen.alphaNumStr.map(ImageListDisplayName.apply)
    private val imageGen = for( a <- imageId; b <- imageSrc; c <- imageAlt) yield Image(a,b,c)
    private val imageListGen = Gen.listOf(imageGen).map(_.groupBy(_.src).values.map(_.head).toList)

    val update: Gen[image.ImageListUpdate] = for {
      id <- listId
      displayName <- Gen.option(displayName)
      images <- Gen.option(imageListGen)
    } yield image.ImageListUpdate(id, displayName, images)

    val self: Gen[image.ImageList] = for {
      id <- listId
      dName <- displayName
      images <- imageListGen
    } yield image.ImageList(id, dName, images)
  }

  object Model {
    private val imListId = Gen.uuid.map(ImageListId.apply)
    private val id = Gen.const(ModelUUID(Gen.uuid.sample.get))
    private val catId = Gen.const(CategoryUUID(Gen.uuid.sample.get))
    private val rId = Gen.alphaNumStr.map(ModelReadableId.apply)
    private val rDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val rDescription = Gen.alphaNumStr.map(ModelDescription.apply)
    private val rMoney = Gen.posNum[Double].map(Money(_, USD)).map(ModelMinimalPrice.apply)

    def create(imListId: ImageListId): Gen[CreateModel] = for {
      rId <- rId
      catId <- catId
      rDisplayName <- rDisplayName
      rDescription <- rDescription
    } yield model.CreateModel(rId, catId, rDisplayName, rDescription, imListId)

    val self: Gen[model.Model] = for {
      id <- id
      rId <- rId
      catId <- catId
      rDisplayName <- rDisplayName
      rDescription <- rDescription
      minPrice <- rMoney
      imgList <- ImageList.self
    } yield model.Model(id, rId, catId, rDisplayName, rDescription, minPrice, imgList)

    val selfLazyList: LazyList[model.Model] = LazyList.continually(Generators.Model.self.sample.get)

    def update(imageListId: ImageListId, categoryId: CategoryUUID): Gen[model.UpdateModel] = for {
      id <- id
      rId <- Gen.option(rId)
      catId <- Gen.option(Gen.const(categoryId))
      rDisplayName <- Gen.option(rDisplayName)
      rDescription <- Gen.option(rDescription)
      imgList <- Gen.option(Gen.const(imageListId))
    } yield model.UpdateModel(id, rId, catId, rDisplayName, rDescription, imgList)
  }

  object PageToken {
    def self: Gen[domain.PageToken] = for {
      size <- Gen.posNum[Long]
      offset <- Gen.posNum[Long]
      res <- Gen.oneOf[domain.PageToken](Gen.const(domain.PageToken.Empty), domain.PageToken.NotEmpty(offset, size))
    } yield res
  }
}
