package ua.pomo.catalog.app

import cats.Show

import java.util.UUID
import cats.implicits.{catsSyntaxOptionId, toShow}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

import scala.util.parsing.combinator._

sealed trait ApiName

object ApiName {
  case object CategoriesName extends ApiName
  case class CategoryName(categoryId: CategoryUUID) extends ApiName
  case class ModelsName(categoryId: Option[CategoryUUID]) extends ApiName
  case class ModelName(categoryId: Option[CategoryUUID], modelId: ModelId) extends ApiName
  case class ImageListName(id: ImageListId) extends ApiName

  import Parsers.parseAllToEither
  case class ParseNameError(message: String) extends Exception(message)
  type NameParseResult[T] = Either[ParseNameError, T]

  def category(s: String): NameParseResult[CategoryName] = parseAllToEither(Parsers.category, s)
  def models(s: String): NameParseResult[ModelsName] = parseAllToEither(Parsers.models, s)
  def model(s: String): NameParseResult[ModelName] = parseAllToEither(Parsers.model, s)
  def imageList(s: String): NameParseResult[ImageListName] = parseAllToEither(Parsers.imageList, s)

  implicit class ToNameString(n: ApiName) {
    def toNameString: String = Show.value.show(n)
  }

  private val Categories: String = "categories"
  private val Models: String = "models"
  private val ImageLists: String = "imageLists"
  private val WildCard: String = "-"

  private object Parsers extends RegexParsers {
    def parseAllToEither[T](p: Parser[T], s: String): NameParseResult[T] = parseAll(p, s) match {
      case Success(matched, _) => Right(matched)
      case Failure(msg, _)     => Left(ParseNameError(msg))
      case Error(msg, _)       => Left(ParseNameError(msg))
    }

    def category: Parser[CategoryName] = Categories ~> "/" ~> categoryUUID ^^ CategoryName.apply
    def models: Parser[ModelsName] = {
      val modelsVar1 = category <~ s"/$Models" ^^ (_.categoryId.some)
      val modelsVar2 = Categories ~> "/" ~> WildCard ^^ (_ => None)
      (modelsVar1 ||| modelsVar2) ^^ ModelsName.apply
    }
    def model: Parser[ModelName] = (models <~ "/") ~ modelUUID ^^ {
      case col ~ id => ModelName(col.categoryId, id)
    }
    def imageList: Parser[ImageListName] = ImageLists ~> "/" ~> imageListId ^^ ImageListName.apply

    private def readableId: Parser[String] = "[^\\/]+".r - uuidStr
    private def uuidStr: Parser[String] = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b".r
    private def uuid: Parser[UUID] = uuidStr ^^ UUID.fromString
    private def categoryUUID = uuid ^^ CategoryUUID.apply
    private def modelUUID = uuid ^^ ModelId.apply
    private def imageListId = uuid ^^ ImageListId.apply
  }

  private object Show {
    private implicit val category: Show[CategoryName] = t => s"$Categories/${t.categoryId.show}"
    private implicit val models: Show[ModelsName] = t => {
      t.categoryId.fold(s"$Categories/$WildCard/$Models") (x => s"${CategoryName(x).show}/$Models")
    }
    private implicit val model: Show[ModelName] = t => s"${ModelsName(t.categoryId).show}/${t.modelId.show}"
    private implicit val imageList: Show[ImageListName] = t => s"$ImageLists/${t.id.show}"
    implicit val value: Show[ApiName] = {
      case x @ CategoryName(_)  => category.show(x)
      case x @ ModelsName(_)    => models.show(x)
      case x @ ModelName(_, _)  => model.show(x)
      case x @ ImageListName(_) => imageList.show(x)
      case CategoriesName => Categories
    }
  }
}
