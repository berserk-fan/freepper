package ua.pomo.catalog.shared

import cats.effect.IO
import doobie.postgres.implicits._
import doobie.implicits.toSqlInterpolator
import org.scalacheck.Gen
import ua.pomo.catalog.domain.category
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter.{ParameterId, ParameterListId}
import ua.pomo.catalog.infrastructure.persistance.postgres.ImageRepositoryImpl
import ua.pomo.common.DbUnitTestSuite
import ua.pomo.common.domain.repository.Repository
import ua.pomo.common.infrastracture.persistance.RepositoryK

import java.util.UUID

trait Fixtures { self: DbUnitTestSuite =>
  protected trait ImageFixture {
    private val repo: Repository[IO, category.Crud] = RepositoryK(ImageRepositoryImpl)
    val images: Seq[Image] = Generators.Image.createListOf5.sample.get
      .map(ImageRepositoryImpl.create(_).trRun())
      .map(ImageRepositoryImpl.get(_).trRun())

    val imagesGen: Gen[List[Image]] = Gen.someOf(images).map(_.toList)
    val imagesGenId: Gen[List[ImageId]] = Gen.someOf(images).map(_.toList).map(_.map(_.id))
  }

  protected trait ImageListFixture extends ImageFixture {
    val imageListRepo: ImageListRepository[IO]
    val imageListId1: ImageListId =
      imageListRepo
        .create(Generators.ImageList.gen(genImages = imagesGen).sample.get)
        .trRun()

    val imageList1: ImageList = imageListRepo.get(imageListId1).trRun()
    val imageListId2: ImageListId =
      imageListRepo.create(Generators.ImageList.gen(genImages = imagesGen).sample.get).trRun()
  }

  protected trait ParameterFixture extends ImageFixture {
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

  protected trait CategoryFixture {
    val categoryRepo: CategoryRepository[IO]

    private val category1: CreateCategory = Generators.Category.create.sample.get
    val categoryId1: CategoryUUID = categoryRepo.create(category1).trRun()

    private val category2: CreateCategory = Generators.Category.create.sample.get
    val categoryId2: CategoryUUID = categoryRepo.create(category2).trRun()
  }

  protected trait ModelFixture extends CategoryFixture with ImageListFixture with ParameterFixture {
    val modelRepo: ModelRepository[IO]
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
