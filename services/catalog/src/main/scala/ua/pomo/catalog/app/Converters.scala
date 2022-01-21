package ua.pomo.catalog.app

import ua.pomo.catalog.api
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model._

object Converters {
  def fromDomain(name: String, cat: Category): api.Category = {
    api.Category(
      name,
      cat.id.value.toString,
      cat.displayName.value,
      cat.description.value
    )
  }

  def fromDomain(name: String, model: Model): api.Model = {
    api.Model(
      name,
      model.id.value.toString,
      model.displayName.value,
      model.description.value,
      model.minimalPrice.value.amount.toInt,
      Some(null)
    )
  }
}
