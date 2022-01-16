package ua.pomo.catalog.api

import ua.pomo.catalog.domain.category.{CategoryId, CategoryReadableId, CategoryUUID}

import java.util.UUID
import scala.util.{Failure, Success, Try}

object NameParser {
  private sealed abstract class Collection(val name: String)
  private object Collection {
    final case object Categories extends Collection("categories")
  }

  def parseCategoryName(name: String): CategoryId = {
    val id = name
      .stripPrefix(Collection.Categories.name)
      .stripPrefix("/")
    Try(UUID.fromString(id)) match {
      case Failure(_) => Right(CategoryReadableId(id))
      case Success(uuid) => Left(CategoryUUID(uuid))
    }
  }
}
