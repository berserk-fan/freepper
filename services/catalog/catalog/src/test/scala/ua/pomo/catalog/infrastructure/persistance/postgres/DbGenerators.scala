package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.Monad
import org.scalacheck.Gen
import ua.pomo.catalog.domain._
import ua.pomo.catalog.domain.category.{Category, CategoryQuery, CategoryUUID, CreateCategory, UpdateCategory}
import ua.pomo.catalog.domain.image.{BuzzImageUpdate, CreateImageData, CreateImageMetadata, Image, ImageId, ImageQuery}
import ua.pomo.catalog.shared.FixturesV2._
import ua.pomo.catalog.shared.Generators
import ua.pomo.common.domain.Generators

object DbGenerators {
  class CategoryGenerators[F[_]: Monad](cf: CategoryFixture[F]#Result) extends Generators[category.Crud.type] {
    override def create: Gen[CreateCategory] = Generators.Category.create

    override def update: Gen[CategoryUUID => UpdateCategory] = Generators.Category.update

    override def genE: Gen[Category] = Generators.Category.gen

    override def id: Gen[CategoryUUID] = Generators.Category.catId

    def query: Gen[CategoryQuery] = Generators.Category.query
  }

  class ImageGenerators[F[_] : Monad](cf: ImageFixture[F]#Result) extends Generators[image.Crud.type] {
    override def create: Gen[CreateImageMetadata] = Generators.Image.create

    override def update: Gen[ImageId => BuzzImageUpdate] = Generators.Image.update

    override def genE: Gen[Image] = Generators.Image.gen

    override def id: Gen[ImageId] = Generators.Image.id

    def query: Gen[ImageQuery] = Generators.Image.query
  }
}
