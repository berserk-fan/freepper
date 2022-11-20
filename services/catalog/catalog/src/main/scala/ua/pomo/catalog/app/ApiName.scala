package ua.pomo.catalog.app

import cats.Show

import java.util.UUID
import cats.implicits.toShow
import derevo.cats.eqv
import derevo.derive
import ua.pomo.catalog.domain.category._
import ua.pomo.common.domain.error.ValidationErr
import ua.pomo.catalog.domain.image.ImageId
import ua.pomo.catalog.domain.imageList._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.product.ProductId

import scala.util.parsing.combinator._

sealed trait ApiName

object ApiName {
  import Parsers.parseAllToEither
  type NameParseResult[T] = Either[ValidationErr, T]

  @derive(eqv)
  sealed trait CategoryRefId {
    def uid: CategoryUUID
  }
  object CategoryRefId {
    case class Readable(rid: CategoryReadableId) extends CategoryRefId {
      override def uid: CategoryUUID = throw new UnsupportedOperationException(s"CategoryRefId was $this, not Uid.")
    }
    case class Uid(uid: CategoryUUID) extends CategoryRefId

    implicit val show: Show[CategoryRefId] = {
      case Readable(value) => value.value.show
      case Uid(value)      => value.value.show
    }
  }

  case object CategoriesName extends ApiName
  case class CategoryName(categoryId: CategoryRefId) extends ApiName
  case class ModelsName(categoryId: CategoryRefId) extends ApiName
  case class ModelName(categoryId: CategoryRefId, modelId: ModelId) extends ApiName
  case object ImageListsName extends ApiName
  case object ImagesName extends ApiName
  case class ImageListName(id: ImageListId) extends ApiName
  case class ImageName(id: ImageId) extends ApiName
  case class ProductsName(categoryId: CategoryRefId, modelId: ModelId) extends ApiName
  case class ProductName(categoryId: CategoryRefId, modelId: ModelId, productId: ProductId) extends ApiName

  implicit class ToNameString(n: ApiName) {
    def toNameString: String = Parsers.Show.value.show(n)
  }

  def parse(s: String): NameParseResult[ApiName] = parseAllToEither(Parsers.any, s)
  def category(s: String): NameParseResult[CategoryName] = parseAllToEither(Parsers.category, s)
  def models(s: String): NameParseResult[ModelsName] = parseAllToEither(Parsers.models, s)
  def model(s: String): NameParseResult[ModelName] = parseAllToEither(Parsers.model, s)
  def imageList(s: String): NameParseResult[ImageListName] = parseAllToEither(Parsers.imageList, s)
  def products(s: String): NameParseResult[ProductsName] = parseAllToEither(Parsers.products, s)
  def product(s: String): NameParseResult[ProductName] = parseAllToEither(Parsers.product, s)
  def imageLists(s: String): NameParseResult[ImageListsName.type] = parseAllToEither(Parsers.imageLists, s)
  def image(s: String): NameParseResult[ImageName] = parseAllToEither(Parsers.image, s)

  private object Parsers extends RegexParsers {
    def any: Parser[ApiName] =
      categories ||| category ||| models ||| model ||| imageLists ||| imageList ||| products ||| product ||| image ||| images

    def parseAllToEither[T](p: Parser[T], s: String): NameParseResult[T] = parseAll(p, s) match {
      case Success(matched, _) => Right(matched)
      case Failure(msg, next)  => createErr(msg, next, fatal = false)
      case Error(msg, next)    => createErr(msg, next, fatal = true)
    }
    def categories: Parser[CategoriesName.type] = s"^$Categories$$".r ^^ (_ => CategoriesName)
    def category: Parser[CategoryName] = Categories ~> "/" ~> categoryId ^^ CategoryName.apply
    def models: Parser[ModelsName] = {
      category <~ s"/$Models" ^^ (_.categoryId) ^^ ModelsName.apply
    }
    def model: Parser[ModelName] = {
      (models <~ "/") ~ modelUUID ^^ { case col ~ id => ModelName(col.categoryId, id) }
    }
    def imageLists: Parser[ImageListsName.type] = s"^$ImageLists$$".r ^^ (_ => ImageListsName)
    def images: Parser[ImagesName.type] = s"^$Images$$".r ^^ (_ => ImagesName)
    def image: Parser[ImageName] = Images ~> "/" ~> imageId ^^ ImageName.apply
    def imageList: Parser[ImageListName] = ImageLists ~> "/" ~> imageListId ^^ ImageListName.apply
    def products: Parser[ProductsName] = {
      model <~ "/" <~ Products ^^ (modelName => ProductsName(modelName.categoryId, modelName.modelId))
    }
    def product: Parser[ProductName] = {
      (products <~ "/") ~ productId ^^ { case productsName ~ productId =>
        ProductName(productsName.categoryId, productsName.modelId, productId)
      }
    }
    private def uuidStr: Parser[String] = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b".r
    private def uuid: Parser[UUID] = uuidStr ^^ UUID.fromString

    private def readableId: Parser[String] = "[a-zA-Z-]+".r
    private def categoryRid: Parser[CategoryReadableId] = readableId ^^ CategoryReadableId.apply
    private def categoryUid: Parser[CategoryUUID] = uuid ^^ CategoryUUID.apply
    private def categoryId: Parser[CategoryRefId] =
      (categoryUid ^^ CategoryRefId.Uid.apply) | (categoryRid ^^ CategoryRefId.Readable.apply)

    private def modelUUID = uuid ^^ ModelId.apply
    private def imageListId = uuid ^^ ImageListId.apply
    private def imageId = uuid ^^ ImageId.apply
    private def productId = uuid ^^ ProductId.apply
    private val Images: String = "images"
    private val Categories: String = "categories"
    private val Models: String = "models"
    private val ImageLists: String = "imageLists"
    private val Products: String = "products"

    object Show {
      private implicit val image: Show[ImageName] = t => s"$Images/${t.id.show}"
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
        case ImagesName               => Images
        case x @ ImageName(_)         => image.show(x)
        case x @ CategoryName(_)      => category.show(x)
        case x @ ModelsName(_)        => models.show(x)
        case x @ ModelName(_, _)      => model.show(x)
        case x @ ImageListName(_)     => imageList.show(x)
        case x @ ProductsName(_, _)   => products.show(x)
        case x @ ProductName(_, _, _) => product.show(x)
        case CategoriesName           => Categories
        case ImageListsName           => ImageLists
      }
    }
  }

  private def createErr[T](msg: String, next: ApiName.Parsers.Input, fatal: Boolean): Left[ValidationErr, Nothing] = {
    Left(
      ValidationErr(
        s"Failed to parse resource name: $msg, string: ${next.source}, fatal: $fatal, rest: ${next.source.toString
          .drop(next.offset)}"
      )
    )
  }
}
