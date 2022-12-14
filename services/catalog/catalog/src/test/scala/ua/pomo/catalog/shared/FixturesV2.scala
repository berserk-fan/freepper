package ua.pomo.catalog.shared

import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits.UuidType
import org.scalacheck.Gen
import ua.pomo.catalog.domain.category.{
  CategoryDescription,
  CategoryDisplayName,
  CategoryReadableId,
  CategoryRepository,
  CategoryUUID,
  CreateCategory
}
import ua.pomo.catalog.domain.image.{Image, ImageId, ImageRepository}
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListId, ImageListRepository}
import ua.pomo.catalog.domain.model.{CreateModel, Model, ModelRepository}
import ua.pomo.catalog.domain.parameter.{ParameterId, ParameterListId}
import cats.{Monad, Traverse, ~>}
import cats.effect.MonadCancelThrow
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps

import java.util.UUID

object FixturesV2 {
  class ImageFixture[F[_]: Monad](imageRepository: ImageRepository[F]) {
    case class Result(
        images: Seq[Image],
        imagesGen: Gen[List[Image]],
        imagesGenId: Gen[List[ImageId]],
        imageIdGen: Gen[ImageId]
    )

    def init(): F[Result] = {
      for {
        images <- Traverse[List].traverse(
          Generators.Image.createListOf5.sample.get
        )(x => imageRepository.create(x).flatMap(x => imageRepository.get(x)))

      } yield {
        val imagesGen: Gen[List[Image]] = Gen.someOf(images).map(_.toList)
        val imagesGenId: Gen[List[ImageId]] = Gen.someOf(images).map(_.toList).map(_.map(_.id))
        Result(images, imagesGen, imagesGenId, Gen.oneOf(images).map(_.id))
      }
    }
  }

  class ImageListFixture[F[_]: Monad](imageFixtureRes: ImageFixture[F]#Result, imageListRepo: ImageListRepository[F]) {
    case class Result(imageListId1: ImageListId, imageList1: ImageList, imageListId2: ImageListId)

    def init(): F[Result] = {
      val existentImagesIdGen = imageFixtureRes.imagesGen.map(_.map(_.id))
      for {
        imageListId1 <- imageListRepo.create(Generators.ImageList.genCreate(genImages = existentImagesIdGen).sample.get)
        i <- imageListRepo.get(imageListId1)
        i2 <- imageListRepo.create(Generators.ImageList.genCreate(genImages = existentImagesIdGen).sample.get)
      } yield Result(imageListId1, i, i2)

    }
  }

  class CategoryFixture[F[_]: Monad](categoryRepo: CategoryRepository[F]) {
    case class Result(categoryId1: CategoryUUID, categoryId2: CategoryUUID)
    def init(): F[Result] = {
      val category1: CreateCategory = CreateCategory(
        Some(CategoryUUID(UUID.randomUUID())),
        CategoryReadableId("category1"),
        CategoryDisplayName("Category 1"),
        CategoryDescription("Some category 1 description")
      )
      val category2: CreateCategory = CreateCategory(
        Some(CategoryUUID(UUID.randomUUID())),
        CategoryReadableId("category2"),
        CategoryDisplayName("Category 2"),
        CategoryDescription("Some category 2 description")
      )

      for { a <- categoryRepo.create(category1); b <- categoryRepo.create(category2) } yield Result(a, b)
    }
  }

  class ParameterFixture[F[_]: MonadCancelThrow](imageFixture: ImageFixture[F]#Result) {
    case class Result(parameterListWithParameter1Id: ParameterListId, parameterId1: ParameterId)

    def init(t: ConnectionIO ~> F): F[Result] = {
      val existentImageId = imageFixture.images.head.id
      for {
        parameterListId1 <- t(
          sql"""insert into parameter_lists (display_name) values ('')""".update
            .withUniqueGeneratedKeys[UUID]("id")
            .map(ParameterListId.apply)
        )
        parameterId1 <-
          t(
            sql"""insert into parameters (display_name, image_id, list_order, parameter_list_id)
                      VALUES ('',${existentImageId.value}, 1, ${parameterListId1.value})""".update
              .withUniqueGeneratedKeys[UUID]("id")
              .map(ParameterId.apply(_))
          )
      } yield Result(parameterListId1, parameterId1)
    }
  }

  class ModelFixture[F[_]: Monad](
      modelRepo: ModelRepository[F],
      ilf: ImageListFixture[F]#Result,
      cf: CategoryFixture[F]#Result,
      pf: ParameterFixture[F]#Result
  ) {

    case class Result(model: Model, modelWithParamList1: Model)

    def init(): F[Result] = {
      for {
        createModel <- Monad[F].pure {
          Generators.Model
            .createGen(ilf.imageListId1, List.empty)
            .sample
            .get
            .copy(categoryId = cf.categoryId1, imageListId = ilf.imageList1.id)
        }
        modelId <- modelRepo.create(createModel)
        model <- modelRepo.get(modelId)
        createModelWithParamList1: CreateModel = Generators.Model
          .createGen(ilf.imageListId1, List.empty)
          .sample
          .get
          .copy(
            categoryId = cf.categoryId1,
            imageListId = ilf.imageList1.id,
            parameterListIds = List(pf.parameterListWithParameter1Id)
          )
        modelWithParamList1Id <- modelRepo.create(createModelWithParamList1)
        modelWithParamList1 <- modelRepo.get(modelWithParamList1Id)
      } yield {
        Result(model, modelWithParamList1)
      }
    }

  }
}
