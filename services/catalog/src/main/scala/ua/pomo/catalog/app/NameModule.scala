package ua.pomo.catalog.app

import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.product.ProductId

import java.util.UUID
import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import scala.util.{Failure, Success, Try}
import Name._

sealed trait Name
object Name {
  final case class CategoryName(categoryId: CategoryId) extends Name
  final case class ModelName(categoryId: Option[CategoryId], modelId: ModelId) extends Name
}

trait NameModule[T <: Name] {
  final def toNameString(t: T): String = {
    substitute.replaceAllIn(
      name,
      (m: Match) => materialize(t).lift(m.group(1)).getOrElse(throw new IllegalStateException("bad name handling")))
  }

  final def of(name: String): Either[String, T] = {
    regex
      .unapplySeq(name)
      .flatMap(of2.lift(_))
      .toRight(s"parsing failure, name=$name doesn't match regex $regex")
  }

  protected def name: String
  protected def materialize(t: T): PartialFunction[String, String]
  protected def of2: PartialFunction[List[String], T]

  protected def parseModelId(modelId: String): ModelId = {
    Try(UUID.fromString(modelId)) match {
      case Failure(_)    => ModelId(ModelReadableId(modelId))
      case Success(uuid) => ModelId(ModelUUID(uuid))
    }
  }

  protected def parseCategoryId(categoryId: String): CategoryId = {
    Try(UUID.fromString(categoryId)) match {
      case Failure(_)    => CategoryId(CategoryReadableId(categoryId))
      case Success(uuid) => CategoryId(CategoryUUID(uuid))
    }
  }

  protected val wildCard = "-"
  private val pathElem = "[^/]*".r
  private val substitute = "\\{(\\w+)}".r
  protected lazy val regex: Regex = {
    new Regex(substitute.replaceAllIn(name, (_: Match) => s"($pathElem)"), substitute.findAllIn(name).toList: _*)
  }
}

object CategoryNameModule extends NameModule[CategoryName] {
  override protected val name: String = "categories/{category}"

  override protected def materialize(name: CategoryName): PartialFunction[String, String] = {
    case "category" => name.categoryId.fold(_.value.toString, _.value)
  }

  override protected def of2: PartialFunction[List[String], CategoryName] = {
    case categoryId :: Nil => CategoryName(parseCategoryId(categoryId))
  }
}

object ModelNameModule extends NameModule[ModelName] {
  override protected val name: String = "categories/{category}/models/{model}"

  override protected def materialize(name: ModelName): PartialFunction[String, String] = {
    case "category" => name.categoryId.fold(wildCard)(_.fold(_.value.toString, _.value))
    case "model"    => name.modelId.fold(_.value.toString, _.value)
  }

  override protected def of2: PartialFunction[List[String], ModelName] = {
    case categoryIdStr :: modelIdStr :: Nil =>
      val categoryId = Option.when(categoryIdStr != wildCard)(parseCategoryId(categoryIdStr))
      val modelId = parseModelId(modelIdStr)
      ModelName(categoryId, modelId)
  }
}

