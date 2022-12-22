package ua.pomo.catalog.shared

import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.{Applicative, Monad, Traverse}
import org.scalacheck.Gen
import org.typelevel.log4cats.Logger
import ua.pomo.catalog.domain.category.{
  CategoryCrud,
  CategoryDescription,
  CategoryDisplayName,
  CategoryId,
  CategoryReadableId,
  CreateCategory
}
import ua.pomo.catalog.domain.image.{ImageCrud, ImageId}
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.{
  CreateModel,
  ModelCrud,
  ModelDescription,
  ModelDisplayName,
  ModelId,
  ModelReadableId
}
import ua.pomo.catalog.domain.parameter.{
  CreateParameter,
  CreateParameterList,
  ParamListDisplayName,
  ParameterDescription,
  ParameterDisplayName,
  ParameterId,
  ParameterListCrud,
  ParameterListId
}
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.catalog.domain.{Registry, image, imageList, parameter, product}
import ua.pomo.common.domain.Fixture
import ua.pomo.common.domain.crud.{Crud, Repository}

import java.util.UUID

object FixturesV2 {

  private def init[F[_]: Applicative, T <: Crud](repo: Repository[F, T], e: List[T#Create]): F[Unit] = {
    Traverse[List].traverse(e)(repo.create).as(())
  }

  def fixtureRegistry[F[_]: Monad: Logger](
      registry: Registry[Lambda[`T <: Crud` => Repository[F, T]]]
  ): F[Registry[Fixture]] =
    for {
      _ <- Logger[F].info("Started to execute fixture object creation")
      _ <- init[F, CategoryCrud](registry.category, CategoryFixture.entities)
      _ <- Logger[F].info("Category migration executed")
      _ <- init[F, ImageCrud](registry.image, ImageFixture.entities)
      _ <- init[F, ImageListCrud](registry.imageList, ImageListFixture.entities)
      _ <- init[F, ParameterListCrud](registry.parameterList, ParameterListFixture.entities)
      _ <- init[F, ModelCrud](registry.model, ModelFixture.entities)
      _ <- init[F, ProductCrud](registry.product, ProductFixture.entities)
      _ <- Logger[F].info("Finished to execute fixture object creation")

    } yield {
      new Registry[Fixture] {
        override def category: Fixture[CategoryCrud] = CategoryFixture

        override def image: Fixture[ImageCrud] = ImageFixture

        override def model: Fixture[ModelCrud] = ModelFixture

        override def product: Fixture[ProductCrud] = ProductFixture

        override def imageList: Fixture[ImageListCrud] = ImageListFixture

        override def parameterList: Fixture[ParameterListCrud] = ParameterListFixture
      }
    }

  object ImageFixture extends Fixture[ImageCrud] {
    val images: List[image.CreateImage] = Generators.Image.createListOf5.sample.get

    override def entities: List[image.CreateImage] = images
  }

  object ImageListFixture extends Fixture[ImageListCrud] {
    private val imageIdsGen: Gen[List[ImageId]] = Gen.someOf(ImageFixture.images.map(_.id.get)).map(_.toList)
    val imageList1: imageList.CreateImageList =
      Generators.ImageList.genCreate(genImages = imageIdsGen).sample.get
    val imageList2: imageList.CreateImageList =
      Generators.ImageList.genCreate(genImages = imageIdsGen).sample.get

    override def entities: List[imageList.CreateImageList] = List(imageList1, imageList2)
  }

  object CategoryFixture extends Fixture[CategoryCrud] {
    val category1: CreateCategory = CreateCategory(
      Some(CategoryId(UUID.randomUUID())),
      CategoryReadableId("category1"),
      CategoryDisplayName("Category 1"),
      CategoryDescription("Some category 1 description")
    )
    val category2: CreateCategory = CreateCategory(
      Some(CategoryId(UUID.randomUUID())),
      CategoryReadableId("category2"),
      CategoryDisplayName("Category 2"),
      CategoryDescription("Some category 2 description")
    )

    override def entities: List[CreateCategory] = List(category1, category2)
  }

  object ParameterListFixture extends Fixture[ParameterListCrud] {
    val pl: parameter.CreateParameterList = CreateParameterList(
      Some(ParameterListId(UUID.randomUUID())),
      ParamListDisplayName("Parameter list 1"),
      List(
        CreateParameter(
          Some(ParameterId(UUID.randomUUID())),
          ParameterDisplayName("Parameter 1"),
          Some(ImageFixture.images.head.id.get),
          ParameterDescription("Parameter 1 description")
        )
      )
    )

    override def entities: List[CreateParameterList] = List(pl)

  }

  object ModelFixture extends Fixture[ModelCrud] {
    val modelWithoutParameterList: CreateModel = CreateModel(
      Some(ModelId(UUID.randomUUID())),
      ModelReadableId("model_1_id"),
      CategoryFixture.category1.id.get,
      ModelDisplayName("model 1 id"),
      ModelDescription("Model 1 description"),
      ImageListFixture.imageList1.id.get,
      List(ParameterListFixture.pl.id.get)
    )

    val modelWithParameterList: CreateModel = CreateModel(
      Some(ModelId(UUID.randomUUID())),
      ModelReadableId("model_2_id"),
      CategoryFixture.category1.id.get,
      ModelDisplayName("model 2 id"),
      ModelDescription("Model 2 description"),
      ImageListFixture.imageList1.id.get,
      List(ParameterListFixture.pl.id.get)
    )

    override def entities: List[CreateModel] = List(modelWithParameterList, modelWithoutParameterList)
  }

  object ProductFixture extends Fixture[ProductCrud] {
    override def entities: List[product.CreateProduct] = List()
  }

  // List(ImageFixture, ImageListFixture, CategoryFixture, ParameterListFixture, ModelFixture)
}
