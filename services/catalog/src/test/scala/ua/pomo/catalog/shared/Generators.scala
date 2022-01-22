package ua.pomo.catalog.shared

import org.scalacheck.Gen
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.{ReadableId, category, image, model}
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.image.{Image, ImageAlt, ImageId, ImageListDisplayName, ImageListId, ImageSrc}

object Generators {
  private val rIdGen: Gen[ReadableId] = {
    Gen.alphaNumStr.filter(_.nonEmpty).map(ReadableId.parse(_).getOrElse(throw new Exception("Gen generated invalid ReadableId")))
  }

  object Category {
    private val catId: Gen[CategoryUUID] = Gen.uuid.map(CategoryUUID.apply)
    private val readableId: Gen[CategoryReadableId] = rIdGen.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)

    val update: Gen[UpdateCategory] = for {
      uuid <- catId
      rId <- readableId
      id <- Gen.oneOf[CategoryId](CategoryId(uuid), CategoryId(rId))
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
    private val id = Gen.const(ModelUUID(Gen.uuid.sample.get))
    private val catId = Gen.const(CategoryUUID(Gen.uuid.sample.get))
    private val rId = rIdGen.map(ModelReadableId.apply)
    private val rDisplayName = Gen.alphaNumStr.map(ModelDisplayName.apply)
    private val rDescription = Gen.alphaNumStr.map(ModelDescription.apply)
    private val rMoney = Gen.posNum[Double].map(Money(_, USD)).map(ModelMinimalPrice.apply)

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
}
