//package ua.pomo.catalog.shared
//
//import ua.pomo.catalog.domain.category._
//import ua.pomo.catalog.domain.model._
//import ua.pomo.catalog.domain.image._
//import ua.pomo.catalog.domain.imageList._
//
//import java.util.UUID
//
//sealed trait FixturesV3
//
//object FixturesV3 {
//  object CatalogFixture extends FixturesV3 {
//    val category = CreateCategory(
//      Some(CategoryUUID(UUID.randomUUID())),
//      CategoryReadableId("category_1"),
//      CategoryDisplayName("Category 1"),
//      CategoryDescription("category description")
//    )
//  }
//
//  object ImageFixture extends FixturesV3 {
//    val image1 = CreateImageMetadata(Some(ImageId(UUID.randomUUID())), ImageSrc("image/1/src"), ImageAlt("image 1"))
//  }
//
//  object ImageListFixture extends FixturesV3 {
//    val imageList = CreateImageList(Some(ImageListId(UUID.randomUUID())), ImageListDisplayName("Image List 1"), List())
//  }
//
//  object ModelFixture extends FixturesV3 {
//    val model = CreateModel(
//      Some(ModelId(UUID.randomUUID())),
//      ModelReadableId("model_1"),
//      CatalogFixture.category.id.get,
//      ModelDisplayName("Model 1"),
//      ModelDescription("Some model. Model 1"),
//      ???,
//      ???
//    )
//  }
//}
