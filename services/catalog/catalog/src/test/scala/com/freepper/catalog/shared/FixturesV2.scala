package com.freepper.catalog.shared

import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.{Applicative, Monad, Traverse}
import org.scalacheck.Gen
import org.typelevel.log4cats.{Logger, LoggerFactory}
import com.freepper.catalog.domain.category.{CategoryCrud, CategoryDescription, CategoryDisplayName, CategoryId, CategoryReadableId, CreateCategory}
import com.freepper.catalog.domain.image.{ImageCrud, ImageId}
import com.freepper.catalog.domain.imageList.ImageListCrud
import com.freepper.catalog.domain.model.{CreateModel, ModelCrud, ModelDescription, ModelDisplayName, ModelId, ModelReadableId}
import com.freepper.catalog.domain.parameter.{CreateParameter, CreateParameterList, ParamListDisplayName, ParameterDescription, ParameterDisplayName, ParameterId, ParameterListCrud, ParameterListId}
import com.freepper.catalog.domain.product.ProductCrud
import com.freepper.catalog.domain.{RegistryHelper, image, imageList, parameter, product}
import com.freepper.common.domain.Fixture
import com.freepper.common.domain.crud.{Crud, Repository}
import com.freepper.common.domain.registry.Registry
import RegistryHelper.implicits._

import java.util.UUID

object FixturesV2 {

  private def init[F[_]: Applicative, T <: Crud](repo: Repository[F, T], e: List[T#Create]): F[Unit] = {
    Traverse[List].traverse(e)(repo.create).as(())
  }

  def fixtureRegistry[F[_]: Monad: LoggerFactory](
      registry: Registry[Lambda[`T <: Crud` => Repository[F, T]]]
  ): F[Registry[Fixture]] =
    for {
      logger <- LoggerFactory[F].create
      _ <- logger.info("Started to execute fixture object creation")
      _ <- init[F, CategoryCrud](registry.category, CategoryFixture.entities)
      _ <- logger.info("Category migration executed")
      _ <- init[F, ImageCrud](registry.image, ImageFixture.entities)
      _ <- init[F, ImageListCrud](registry.imageList, ImageListFixture.entities)
      _ <- init[F, ParameterListCrud](registry.parameterList, ParameterListFixture.entities)
      _ <- init[F, ModelCrud](registry.model, ModelFixture.entities)
      _ <- init[F, ProductCrud](registry.product, ProductFixture.entities)
      _ <- logger.info("Finished to execute fixture object creation")

    } yield RegistryHelper.createRegistry[Fixture](
      CategoryFixture,
      ImageFixture,
      ImageListFixture,
      ModelFixture,
      ProductFixture,
      ParameterListFixture
    )

  object ImageFixture extends Fixture[ImageCrud] {
    val images: List[image.CreateImage] = Generators.Image.createListOf5.sample.get

    override val entities: List[image.CreateImage] = images
  }

  object ImageListFixture extends Fixture[ImageListCrud] {
    private val imageIdsGen: Gen[List[ImageId]] = Gen.someOf(ImageFixture.images.map(_.id)).map(_.toList)
    val imageList1: imageList.CreateImageList =
      Generators.ImageList.genCreate(genImages = imageIdsGen).sample.get
    val imageList2: imageList.CreateImageList =
      Generators.ImageList.genCreate(genImages = imageIdsGen).sample.get

    override def entities: List[imageList.CreateImageList] = List(imageList1, imageList2)
  }

  object CategoryFixture extends Fixture[CategoryCrud] {
    val category1: CreateCategory = CreateCategory(
      CategoryId(UUID.randomUUID()),
      CategoryReadableId("category1"),
      CategoryDisplayName("Category 1"),
      CategoryDescription("Some category 1 description")
    )
    val category2: CreateCategory = CreateCategory(
      CategoryId(UUID.randomUUID()),
      CategoryReadableId("category2"),
      CategoryDisplayName("Category 2"),
      CategoryDescription("Some category 2 description")
    )

    override def entities: List[CreateCategory] = List(category1, category2)
  }

  object ParameterListFixture extends Fixture[ParameterListCrud] {
    val pl: parameter.CreateParameterList = CreateParameterList(
      ParameterListId(UUID.randomUUID()),
      ParamListDisplayName("Parameter list 1"),
      List(
        CreateParameter(
          ParameterId(UUID.randomUUID()),
          ParameterDisplayName("Parameter 1"),
          Some(ImageFixture.images.head.id),
          Some(ParameterDescription("Parameter 1 description"))
        )
      )
    )

    override def entities: List[CreateParameterList] = List(pl)

  }

  object ModelFixture extends Fixture[ModelCrud] {
    val modelWithoutParameterList: CreateModel = CreateModel(
      ModelId(UUID.randomUUID()),
      ModelReadableId("model_1_id"),
      CategoryFixture.category1.id,
      ModelDisplayName("model 1 id"),
      ModelDescription("Model 1 description"),
      ImageListFixture.imageList1.id,
      List(ParameterListFixture.pl.id)
    )

    val modelWithParameterList: CreateModel = CreateModel(
      ModelId(UUID.randomUUID()),
      ModelReadableId("model_2_id"),
      CategoryFixture.category1.id,
      ModelDisplayName("model 2 id"),
      ModelDescription("Model 2 description"),
      ImageListFixture.imageList1.id,
      List(ParameterListFixture.pl.id)
    )

    override def entities: List[CreateModel] = List(modelWithParameterList, modelWithoutParameterList)
  }

  object ProductFixture extends Fixture[ProductCrud] {
    override def entities: List[product.CreateProduct] = List()
  }

  // List(ImageFixture, ImageListFixture, CategoryFixture, ParameterListFixture, ModelFixture)
}
