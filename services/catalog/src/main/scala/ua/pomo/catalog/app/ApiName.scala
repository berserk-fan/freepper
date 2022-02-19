package ua.pomo.catalog.app

import cats.Show

import java.util.UUID
import cats.implicits.{catsSyntaxOptionId, toShow}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.error.ValidationErr
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.product.ProductId

import scala.util.parsing.combinator._

sealed trait ApiName

object ApiName {
  case object CategoriesName extends ApiName
  case class CategoryName(categoryId: CategoryUUID) extends ApiName
  case class ModelsName(categoryId: CategoryUUID) extends ApiName
  case class ModelName(categoryId: CategoryUUID, modelId: ModelId) extends ApiName
  case class ImageListName(id: ImageListId) extends ApiName
  case class ProductsName(categoryId: CategoryUUID, modelId: ModelId) extends ApiName
  case class ProductName(categoryId: CategoryUUID, modelId: ModelId, productId: ProductId) extends ApiName

  import Parsers.parseAllToEither
  type NameParseResult[T] = Either[ValidationErr, T]

  def category(s: String): NameParseResult[CategoryName] = parseAllToEither(Parsers.category, s)
  def models(s: String): NameParseResult[ModelsName] = parseAllToEither(Parsers.models, s)
  def model(s: String): NameParseResult[ModelName] = parseAllToEither(Parsers.model, s)
  def imageList(s: String): NameParseResult[ImageListName] = parseAllToEither(Parsers.imageList, s)
  def products(s: String): NameParseResult[ProductsName] = parseAllToEither(Parsers.products, s)
  def product(s: String): NameParseResult[ProductName] = parseAllToEither(Parsers.product, s)

  implicit class ToNameString(n: ApiName) {
    def toNameString: String = Show.value.show(n)
  }

  private val Categories: String = "categories"
  private val Models: String = "models"
  private val ImageLists: String = "imageLists"
  private val Products: String = "products"

  private object Parsers extends RegexParsers {
    def parseAllToEither[T](p: Parser[T], s: String): NameParseResult[T] = parseAll(p, s) match {
      case Success(matched, _) => Right(matched)
      case Failure(msg, _)     => Left(ValidationErr(s"Failed to parse resource name: $msg"))
      case Error(msg, _)       => Left(ValidationErr(s"Failed to parse resource name: $msg"))
    }

    def category: Parser[CategoryName] = Categories ~> "/" ~> categoryUUID ^^ CategoryName.apply
    def models: Parser[ModelsName] = {
      category <~ s"/$Models" ^^ (_.categoryId) ^^ ModelsName.apply
    }
    def model: Parser[ModelName] = {
      (models <~ "/") ~ modelUUID ^^ { case col ~ id => ModelName(col.categoryId, id) }
    }
    def imageList: Parser[ImageListName] = ImageLists ~> "/" ~> imageListId ^^ ImageListName.apply
    def products: Parser[ProductsName] = {
      model <~ "/" <~ Products ^^ (modelName => ProductsName(modelName.categoryId, modelName.modelId))
    }
    def product: Parser[ProductName] = {
      (products <~ "/") ~ productId ^^ {
        case productsName ~ productId => ProductName(productsName.categoryId, productsName.modelId, productId)
      }
    }

    private def uuidStr: Parser[String] = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b".r
    private def uuid: Parser[UUID] = uuidStr ^^ UUID.fromString
    private def categoryUUID = uuid ^^ CategoryUUID.apply
    private def modelUUID = uuid ^^ ModelId.apply
    private def imageListId = uuid ^^ ImageListId.apply
    private def productId = uuid ^^ ProductId.apply
  }

  private object Show {
    private implicit val category: Show[CategoryName] = t => s"$Categories/${t.categoryId.show}"
    private implicit val models: Show[ModelsName] = t => {
      s"${CategoryName(t.categoryId).show}/$Models"
    }
    private implicit val model: Show[ModelName] = t => s"${ModelsName(t.categoryId).show}/${t.modelId.show}"
    private implicit val imageList: Show[ImageListName] = t => s"$ImageLists/${t.id.show}"
    private implicit val products: Show[ProductsName] = t => s"${ModelName(t.categoryId, t.modelId).show}/$Products"
    private implicit val product: Show[ProductName] = t =>
      s"${ProductsName(t.categoryId, t.modelId).show}/${t.productId}"

    implicit val value: Show[ApiName] = {
      case x @ CategoryName(_)      => category.show(x)
      case x @ ModelsName(_)        => models.show(x)
      case x @ ModelName(_, _)      => model.show(x)
      case x @ ImageListName(_)     => imageList.show(x)
      case x @ ProductsName(_, _)   => products.show(x)
      case x @ ProductName(_, _, _) => product.show(x)
      case CategoriesName           => Categories
    }
  }
}
