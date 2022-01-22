package ua.pomo.catalog.app

import ua.pomo.catalog.api
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.image._

import ApiName._

object Converters {
  def fromDomain(cat: Category): api.Category = {
    api.Category(
      CategoryName(CategoryId(cat.id)).toNameString,
      cat.id.value.toString,
      cat.displayName.value,
      cat.description.value
    )
  }

  def toApi(model: Model): api.Model = {
    api.Model(
      ModelName(None, ModelId(model.id)).toNameString,
      model.id.value.toString,
      model.displayName.value,
      model.description.value,
      model.minimalPrice.value.amount.toInt,
      Some(null)
    )
  }

  def fromDomain(imageList: ImageList): api.ImageList = {
    api.ImageList(
      
    )
  }
}
