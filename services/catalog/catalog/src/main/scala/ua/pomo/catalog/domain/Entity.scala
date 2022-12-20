package ua.pomo.catalog.domain

import ua.pomo.common.domain.repository.EntityDisplayName

sealed abstract class Entity(val name: EntityDisplayName)
object Entity {
  case object Category extends Entity(EntityDisplayName("category"))
  case object Image extends Entity(EntityDisplayName("image"))
  case object ImageList extends Entity(EntityDisplayName("imageList"))
  case object Model extends Entity(EntityDisplayName("model"))
  case object Product extends Entity(EntityDisplayName("product"))
  case object ParameterList extends Entity(EntityDisplayName("parameterList"))

  private def all: List[Entity] = List(Category, ImageList, Image, Model, Product, ParameterList)
  def fromName(n: EntityDisplayName): Option[Entity] = all.find(_.name == n)
}
