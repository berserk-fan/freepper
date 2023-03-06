package com.freepper.catalog.infrastructure.persistance.postgres

import com.freepper.catalog.shared.Generators
import org.scalacheck.Gen
import com.freepper.catalog.domain._
import com.freepper.catalog.domain.category._
import com.freepper.catalog.domain.image._
import com.freepper.catalog.domain.imageList._
import com.freepper.catalog.domain.model.ModelCrud
import com.freepper.catalog.domain.parameter._
import com.freepper.catalog.domain.product.ProductCrud
import com.freepper.catalog.shared.FixturesV2._
import com.freepper.common.domain.registry.Registry
import com.freepper.common.domain.{Generators, crud}

object DbGenerators {

  val generatorRegistry: Registry[Generators] = RegistryHelper.createRegistry(
    CategoryGenerators,
    ImageGenerators,
    ImageListGenerators,
    ModelGenerators,
    ProductGenerators,
    ParameterListGenerators
  )

  private case object CategoryGenerators extends Generators[CategoryCrud] {
    override def create: Gen[CreateCategory] = Generators.Category.create

    override def update: Gen[CategoryId => UpdateCategory] = Generators.Category.update

    override def genE: Gen[Category] = Generators.Category.gen

    override def id: Gen[CategoryId] = Generators.Category.catId

    override def query: Gen[CategoryQuery] = Generators.Category.query
  }

  private case object ImageGenerators extends Generators[ImageCrud] {
    override def create: Gen[CreateImage] = Generators.Image.create

    override def update: Gen[ImageId => BuzzImageUpdate] = Generators.Image.update

    override def genE: Gen[Image] = Generators.Image.gen

    override def id: Gen[ImageId] = Generators.Image.id

    override def query: Gen[ImageQuery] = Generators.Image.query
  }

  private case object ImageListGenerators extends Generators[ImageListCrud] {
    override def update: Gen[ImageListId => UpdateImageList] =
      Generators.ImageList.update(Gen.someOf(ImageFixture.images.map(_.id)).map(_.toList))

    override def genE: Gen[ImageList] = Generators.ImageList.gen(Gen.listOf(Generators.Image.gen).map(_.toList))

    override def create: Gen[CreateImageList] =
      Generators.ImageList.genCreate(Gen.someOf(ImageFixture.images.map(_.id)).map(_.toList))

    override def query: Gen[crud.Query[ImageListSelector]] = Generators.ImageList.query

    override def id: Gen[ImageListId] = Generators.ImageList.id
  }

  private case object ParameterListGenerators extends Generators[ParameterListCrud] {
    override def create: Gen[CreateParameterList] =
      Generators.ParameterList.create(Gen.oneOf(ImageFixture.images.map(_.id)))

    override def update: Gen[ParameterListId => UpdateParameterList] =
      Generators.ParameterList.update(Gen.oneOf(ImageFixture.images.map(_.id)))
    override def genE: Gen[parameter.ParameterList] = Generators.ParameterList.paramList
    override def id: Gen[ParameterListId] = Generators.ParameterList.paramListId
    override def query: Gen[ParameterListQuery] = Generators.ParameterList.query
  }

  private case object ModelGenerators extends Generators[ModelCrud] {
    override def create: Gen[model.CreateModel] =
      Generators.Model.createGen(
        ImageListFixture.imageList1.id,
        List(ParameterListFixture.pl.id),
        Gen.const(CategoryFixture.category1.id)
      )

    override def update: Gen[model.ModelId => model.UpdateModel] =
      Generators.Model.updateGen(ImageListFixture.imageList1.id, CategoryFixture.category1.id)

    override def genE: Gen[model.Model] = Generators.Model.gen

    override def id: Gen[model.ModelId] = Generators.Model.id

    override def query: Gen[crud.Query[model.ModelSelector]] = Generators.Model.query
  }

  private case object ProductGenerators extends Generators[ProductCrud] {

    override def update: Gen[product.ProductId => product.UpdateProduct] =
      Generators.Product.update(ImageListFixture.imageList2.id)

    override def genE: Gen[product.Product] = Generators.Product.gen

    override def id: Gen[product.ProductId] = Generators.Product.id

    override def query: Gen[crud.Query[product.ProductSelector]] = Generators.Product.query

    override def create: Gen[product.CreateProduct] = {
      Generators.Product.create(
        ImageListFixture.imageList1.id,
        ModelFixture.modelWithParameterList.id,
        List(ParameterListFixture.pl.parameters.head.id)
      )
    }
  }
}
