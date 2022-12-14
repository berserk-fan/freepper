package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.Monad
import org.scalacheck.Gen
import ua.pomo.catalog.domain.{model, _}
import ua.pomo.catalog.domain.category.{
  Category,
  CategoryCrud,
  CategoryQuery,
  CategoryUUID,
  CreateCategory,
  UpdateCategory
}
import ua.pomo.catalog.domain.image.{
  BuzzImageUpdate,
  CreateImageData,
  CreateImageMetadata,
  Image,
  ImageCrud,
  ImageId,
  ImageQuery
}
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter._
import ua.pomo.catalog.shared.FixturesV2._
import ua.pomo.catalog.shared.Generators
import ua.pomo.common.domain.{Generators, repository}

object DbGenerators {
  case class CategoryGenerators() extends Generators[CategoryCrud] {
    override def create: Gen[CreateCategory] = Generators.Category.create

    override def update: Gen[CategoryUUID => UpdateCategory] = Generators.Category.update

    override def genE: Gen[Category] = Generators.Category.gen

    override def id: Gen[CategoryUUID] = Generators.Category.catId

    def query: Gen[CategoryQuery] = Generators.Category.query
  }

  case class ImageGenerators() extends Generators[ImageCrud] {
    override def create: Gen[CreateImageMetadata] = Generators.Image.create

    override def update: Gen[ImageId => BuzzImageUpdate] = Generators.Image.update

    override def genE: Gen[Image] = Generators.Image.gen

    override def id: Gen[ImageId] = Generators.Image.id

    override def query: Gen[ImageQuery] = Generators.Image.query
  }

  case class ImageListGenerators[F[_]: Monad](cf: ImageFixture[F]#Result) extends Generators[ImageListCrud] {
    override def update: Gen[ImageListId => UpdateImageList] = Generators.ImageList.update(cf.imagesGenId)

    override def genE: Gen[ImageList] = Generators.ImageList.gen(cf.imagesGen)

    override def create: Gen[CreateImageList] = Generators.ImageList.genCreate(cf.imagesGenId)

    override def query: Gen[repository.Query[ImageListSelector]] = Generators.ImageList.query

    override def id: Gen[ImageListId] = Generators.ImageList.id
  }

  case class ParameterList[F[_]](cf: ImageFixture[F]#Result) extends Generators[ParameterListCrud] {
    override def create: Gen[CreateParameterList] = Generators.ParameterList.create(cf.imageIdGen)
    override def update: Gen[ParameterListId => UpdateParameterList] = Generators.ParameterList.update(cf.imageIdGen)
    override def genE: Gen[parameter.ParameterList] = Generators.ParameterList.paramList
    override def id: Gen[ParameterListId] = Generators.ParameterList.paramListId
    override def query: Gen[ParameterListQuery] = Generators.ParameterList.query
  }
}
