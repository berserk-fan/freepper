package ua.pomo.catalog.api

import ua.pomo.catalog.domain.category

object Converters {
  def fromDomain(name: String, cat: category.Category): Category = {
    Category(
      name,
      cat.id.value.toString,
      cat.displayName.value,
      cat.description.value
    )
  }
}
