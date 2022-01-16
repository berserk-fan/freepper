package ua.pomo.catalog.shared

import org.scalacheck.Gen
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.category
import ua.pomo.catalog.domain.image
import ua.pomo.catalog.domain.image.{Image, ImageAlt, ImageId, ImageListDisplayName, ImageListId, ImageSrc}

object Generators {
  object Category {
    private val uuid: Gen[CategoryUUID] = Gen.uuid.map(CategoryUUID.apply)
    private val readableId: Gen[CategoryReadableId] = Gen.alphaNumStr.map(CategoryReadableId.apply)
    private val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    private val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)

    val update: Gen[UpdateCategory] = for {
      uuid <- uuid
      rId <- readableId
      id <- Gen.oneOf[CategoryId](CategoryId(uuid), CategoryId(rId))
      readableId <- Gen.option(Generators.Category.readableId)
      displayName <- Gen.option(Generators.Category.displayName)
      descr <- Gen.option(Generators.Category.description)
    } yield UpdateCategory(id, readableId, displayName, descr)

    val self: Gen[Category] = for {
      id <- uuid
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
}
