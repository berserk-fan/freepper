package ua.pomo.catalog.app.programs.modifiers

import cats.effect.Sync
import cats.implicits.{catsSyntaxTuple2Semigroupal, toFlatMapOps, toFunctorOps}
import scalapb.descriptors.{FieldDescriptor, PString, PValue}
import ua.pomo.catalog.app.ApiName
import ua.pomo.catalog.app.ApiName.CategoryRefId
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.{CategoryQuery, CategoryRepository, CategorySelector}
import ua.pomo.catalog.domain.error.NotFound

case class ReadableIdInNamesResolver[F[_]: Sync](categoryRepository: CategoryRepository[F]) extends MessageModifier[F] {
  private def resolve(categoryId: CategoryRefId): F[CategoryRefId] = categoryId match {
    case CategoryRefId.Readable(rid) =>
      categoryRepository
        .query(CategoryQuery(CategorySelector.RidIs(rid), PageToken.One))
        .flatMap { foundCategories =>
          foundCategories.headOption
            .fold[F[CategoryRefId]](Sync[F].raiseError[CategoryRefId](NotFound("category", rid))) { category =>
              Sync[F].pure(CategoryRefId.Uid(category.id))
            }
        }
    case x @ CategoryRefId.Uid(_) => Sync[F].pure(x)
  }

  private def resolveName(name: String): F[String] = {
    import ApiName._

    val mapper: PartialFunction[ApiName, F[ApiName]] = {
      case CategoryName(categoryId)       => resolve(categoryId).map(CategoryName)
      case ModelsName(categoryId)         => resolve(categoryId).map(ModelsName)
      case ModelName(categoryId, modelId) => (resolve(categoryId), Sync[F].pure(modelId)).mapN(ModelName)
      case x                              => Sync[F].pure[ApiName](x)
    }

    Sync[F]
      .fromEither(ApiName.parse(name))
      .flatMap(mapper)
      .map(_.toNameString)
  }

  override def names: List[String] = List("parent", "name")
  override def transformation[T <: PValue](field: FieldDescriptor, v: T): F[T] = {
    v match {
      case e: PString =>
        val newName = if (e.value.isEmpty) {
          Sync[F].pure(e.value)
        } else {
          resolveName(e.value)
        }
        newName.map(PString(_).asInstanceOf[T])
      case _ => Sync[F].raiseError(new IllegalArgumentException(s"$names fields are expected to be of type string"))
    }
  }
}
