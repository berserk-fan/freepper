package ua.pomo.catalog.infrastructure.persistance.postgres

import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.catalog.domain.{model, _}
import ua.pomo.common.domain.Assertions

object CatalogAssertions extends Matchers {
  val registry: Registry[Assertions] = new Registry[Assertions] {
    override def category: Assertions[CategoryCrud] = catCheckers

    override def image: Assertions[ImageCrud] = imageAssertions

    override def imageList: Assertions[ImageListCrud] = imageListAssertions

    override def model: Assertions[ModelCrud] = modelAssertions

    override def product: Assertions[ProductCrud] = productAssertions

    override def parameterList: Assertions[ParameterListCrud] = plAssertions
  }

  private val catCheckers: Assertions[CategoryCrud] = new Assertions[CategoryCrud] {
    def update(c: UpdateCategory, v: Category): Any = {
      c.readableId.foreach(_ should ===(v.readableId))
      c.description.foreach(_ should ===(v.description))
      c.displayName.foreach(_ should ===(v.displayName))
    }

    def create(c: CreateCategory, v: Category): Any = {
      c.readableId should ===(v.readableId)
      c.displayName should ===(v.displayName)
      c.description should ===(v.description)
    }
  }

  private val imageAssertions: Assertions[ImageCrud] = new Assertions[ImageCrud] {
    def update(c: BuzzImageUpdate, v: Image): Any = succeed

    def create(c: CreateImage, v: Image): Any = {
      c.src should ===(v.src)
      c.alt should ===(v.alt)
    }
  }

  private val imageListAssertions: Assertions[ImageListCrud] = new Assertions[ImageListCrud] {
    override def update(c: imageList.UpdateImageList, v: imageList.ImageList): Any = {
      c.displayName.foreach(_ should ===(v.displayName))
      c.images.foreach(_ should ===(v.images.map(_.id)))
    }

    override def create(c: imageList.CreateImageList, v: imageList.ImageList): Any = {
      c.displayName should ===(v.displayName)
      c.images should ===(v.images.map(_.id))
    }
  }

  private val plAssertions: Assertions[ParameterListCrud] = new Assertions[ParameterListCrud] {
    override def update(c: parameter.UpdateParameterList, v: parameter.ParameterList): Any = {
      c.displayName.foreach(_ should equal(v.displayName))

      c.parameters.foreach { parametersUpdate =>
        parametersUpdate.map(_.id.get) should equal(v.parameters.map(_.id))

        parametersUpdate.foreach { paramUpdate =>
          val updated = v.parameters.find(_.id == paramUpdate.id.get).get
          paramUpdate.displayName should equal(updated.displayName)
          paramUpdate.image should equal(updated.image.map(_.id))
          paramUpdate.description should equal(updated.description)
        }
      }
    }

    override def create(c: parameter.CreateParameterList, v: parameter.ParameterList): Any = {
      c.displayName should equal(v.displayName)

      c.parameters.map(cp => (cp.displayName, cp.image, cp.description)) should equal(
        v.parameters.map(p => (p.displayName, p.image.map(_.id), p.description))
      )
    }
  }

  private val modelAssertions: Assertions[ModelCrud] = new Assertions[ModelCrud] {
    override def update(c: model.UpdateModel, v: model.Model): Any = {
      c.displayName.foreach(_ should ===(v.displayName))
      c.readableId.foreach(_ should ===(v.readableId))
      c.description.foreach(_ should ===(v.description))
      c.categoryId.foreach(_ should ===(v.categoryUid))
      c.imageListId.foreach(_ should ===(v.imageList.id))
    }

    override def create(c: model.CreateModel, v: model.Model): Any = {
      c.displayName should ===(v.displayName)
      c.readableId should ===(v.readableId)
      c.description should ===(v.description)
      c.categoryId should ===(v.categoryUid)
      c.imageListId should ===(v.imageList.id)
      c.parameterListIds should ===(v.parameterLists.map(_.id))
    }
  }

  private val productAssertions: Assertions[ProductCrud] = new Assertions[ProductCrud] {
    override def update(c: product.UpdateProduct, v: product.Product): Any = {
      c.imageListId.foreach(_ should ===(v.imageList.id))
      c.price.foreach(_ should ===(v.price.standard))
      c.promoPrice.foreach(_ should ===(v.price.promo))
    }

    override def create(c: product.CreateProduct, v: product.Product): Any = {
      c.imageListId should ===(v.imageList.id)
      c.modelId should ===(v.modelId)
      c.priceUsd should ===(v.price.standard)
      c.promoPriceUsd should ===(v.price.promo)
      c.parameterIds should ===(v.parameterIds)
    }
  }
}
