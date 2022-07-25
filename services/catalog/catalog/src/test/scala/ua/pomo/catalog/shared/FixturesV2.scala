package ua.pomo.catalog.shared

import cats.effect.IO
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits.UuidType
import org.scalacheck.Gen
import ua.pomo.catalog.domain.category.{CategoryRepository, CategoryUUID, CreateCategory}
import ua.pomo.catalog.domain.image
import ua.pomo.catalog.domain.image.{Image, ImageId}
import ua.pomo.catalog.domain.imageList.{ImageList, ImageListId, ImageListRepository}
import ua.pomo.catalog.domain.model.{CreateModel, ModelRepository}
import ua.pomo.catalog.domain.parameter.{ParameterId, ParameterListId}
import ua.pomo.common.UnsafeRunnable
import ua.pomo.common.UnsafeRunnable.UnsafeRunnableSyntax
import ua.pomo.common.domain.repository.Repository

import java.util.UUID

object FixturesV2 {
  import UnsafeRunnable._

  class AbstractFixture()(implicit ops: UnsafeRunnable[IO])

  trait ImageFixture[F[_]] { self: AbstractFixture =>
    def imageRepository: Repository[IO, image.Crud.type]
    val images: Seq[Image] = Generators.Image.createListOf5.sample.get
      .map(imageRepository.create(_).trRun())
      .map(imageRepository.get(_).trRun())

    val imagesGen: Gen[List[Image]] = Gen.someOf(images).map(_.toList)
    val imagesGenId: Gen[List[ImageId]] = Gen.someOf(images).map(_.toList).map(_.map(_.id))
  }

  trait ImageListFixture extends ImageFixture { self: AbstractFixture =>
    val imageListRepo: ImageListRepository[ConnectionIO]
    val imageListId1: ImageListId =
      imageListRepo
        .create(Generators.ImageList.gen(genImages = imagesGen).sample.get)
        .trRun()

    val imageList1: ImageList = imageListRepo.get(imageListId1).trRun()
    val imageListId2: ImageListId =
      imageListRepo.create(Generators.ImageList.gen(genImages = imagesGen).sample.get).trRun()
  }

  trait ParameterFixture extends ImageFixture { self: AbstractFixture =>
    val parameterListWithParameter1Id = sql"""insert into parameter_lists (display_name) values ('')""".update
      .withUniqueGeneratedKeys[UUID]("id")
      .map(ParameterListId.apply)
      .trRun()
    val parameter1ImageId = images.head.id
    val parameterId1 =
      sql"""insert into parameters (display_name, image_id, list_order, parameter_list_id) 
            VALUES ('',${parameter1ImageId.value}, 1, ${parameterListWithParameter1Id.value})""".update
        .withUniqueGeneratedKeys[UUID]("id")
        .map(ParameterId.apply)
        .trRun()
  }

  trait CategoryFixture { self: AbstractFixture =>
    val categoryRepo: CategoryRepository[ConnectionIO]

    private val category1: CreateCategory = Generators.Category.create.sample.get
    val categoryId1: CategoryUUID = categoryRepo.create(category1).trRun()

    private val category2: CreateCategory = Generators.Category.create.sample.get
    val categoryId2: CategoryUUID = categoryRepo.create(category2).trRun()
  }

  trait ModelFixture extends CategoryFixture with ImageListFixture with ParameterFixture { self: AbstractFixture =>
    val modelRepo: ModelRepository[ConnectionIO]
    private val createModel: CreateModel =
      Generators.Model
        .createGen(imageListId1, List.empty)
        .sample
        .get
        .copy(categoryId = categoryId1, imageListId = imageList1.id)
    val modelId = modelRepo.create(createModel).trRun()

    private val modelWithParamList1 = Generators.Model
      .createGen(imageListId1, List.empty)
      .sample
      .get
      .copy(
        categoryId = categoryId1,
        imageListId = imageList1.id,
        parameterListIds = List(parameterListWithParameter1Id)
      )
    val modelWithParamList1Id = modelRepo.create(modelWithParamList1).trRun()
  }
}
