package ua.pomo.catalog.app

import cats.effect.Sync
import cats.implicits.{
  catsSyntaxApplicativeErrorId,
  catsSyntaxApplicativeId,
  catsSyntaxTuple2Semigroupal,
  toFlatMapOps,
  toFunctorOps,
  toTraverseOps
}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import scalapb.descriptors.{FieldDescriptor, PMessage, PString, PValue}
import ua.pomo.catalog.app.ApiName.CategoryRefId
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.{CategoryQuery, CategoryRepository, CategorySelector}
import ua.pomo.catalog.domain.error.NotFound

trait MessageModifier[F[_]] {
  def modify[T <: GeneratedMessage: GeneratedMessageCompanion](m: T): F[T]
}

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

  private def modifyHelper(v: PMessage): F[PMessage] = {
    v.value
      .map[F[(FieldDescriptor, PValue)]] {
        case (descriptor, value) =>
          value match {
            case nested: PMessage => modifyHelper(nested).map((descriptor, _))
            case _ if descriptor.name == "name" || descriptor.name == "parent" =>
              Sync[F]
                .delay(value.as[String])
                .flatMap { x =>
                  if (x.isEmpty) {
                    Sync[F].pure(x)
                  } else {
                    resolveName(x)
                  }
                }
                .map(x => (descriptor, PString(x)))
            case x => Sync[F].pure((descriptor, x))
          }
      }
      .toSeq
      .sequence
      .map(x => PMessage(x.toMap))
  }

  override def modify[T <: GeneratedMessage: GeneratedMessageCompanion](g: T): F[T] = {
    val c = implicitly[GeneratedMessageCompanion[T]]
    modifyHelper(g.toPMessage).map(c.messageReads.read)
  }
}
