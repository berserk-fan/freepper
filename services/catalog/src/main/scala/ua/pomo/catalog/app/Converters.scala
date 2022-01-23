package ua.pomo.catalog.app

import ua.pomo.catalog.api
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.image._
import ApiName._
import cats.implicits.toShow
import squants.market.{Money, USD}
import ua.pomo.catalog.api.CreateModelRequest

import java.util.UUID

object Converters {
  def toApi(cat: Category): api.Category = {
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
      model.id.show,
      model.readableId.show,
      model.displayName.show,
      model.description.value,
      model.minimalPrice.value.amount.toInt,
      Some(null)
    )
  }

  def toDomain(request: CreateModelRequest, categoryId: CategoryUUID): CreateModel = {
    val model = request.model.get
    val imageListId = ApiName.imageList(model.imageList.get.name).getOrElse(throw new NoSuchElementException()).id

    CreateModel(
      ModelReadableId(model.readableId),
      categoryId,
      ModelDisplayName(model.displayName),
      ModelDescription(model.description),
      imageListId
    )
  }
}
