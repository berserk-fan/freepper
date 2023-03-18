package com.freepper.common.infrastracture.persistance.inmemory

import cats.{MonadThrow, Show}
import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import shapeless._
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import com.freepper.common.domain.crud._
import com.freepper.common.domain.error.NotFound

abstract class AbstractInMemoryRepository[F[_]: MonadThrow, T <: Crud: ValueOf](ref: Ref[F, Map[T#EntityId, T#Entity]])(
    implicit
    updateToEntityId: monocle.Getter[T#Update, T#EntityId],
    entityToEntityId: monocle.Getter[T#Entity, T#EntityId],
    show: Show[T]
) extends Repository[F, T] {
  protected def creator: T#Create => T#Entity
  protected def filter: T#Selector => T#Entity => Boolean

  override def create(req: T#Create): F[T#EntityId] = ref.modify { map =>
    val entity = creator(req)
    val id = entityToEntityId.get(entity)
    val newMap = map + (id -> entity)
    (newMap, id)
  }

  override def get(id: T#EntityId): F[T#Entity] = {
    val entityDisplayName: String = Show[T].show(implicitly[ValueOf[T]].value)

    find(id).flatMap(
      _.fold(NotFound(entityDisplayName, id).raiseError[F, T#Entity])(_.pure[F])
    )
  }

  override def find(id: T#EntityId): F[Option[T#Entity]] = ref.get.map(_.get(id))

  override def findAll(req: Query[T#Selector]): F[List[T#Entity]] = {
    val filter1: T#Entity => Boolean = filter(req.selector)
    ref.get
      .map(
        _.values.toList
          .filter(filter1)
          .slice(req.page.offset.toInt, req.page.offset.toInt + req.page.size.toInt)
      )
  }

  override def delete(id: T#EntityId): F[Int] = ref.modify { map =>
    // qq
    map.get(id).fold((map, 0))(_ => (map - id, 1))
  }

  protected def updateHelper[U <: HList, U2 <: HList, U3 <: HList, V <: Poly1](
      command: T#Update,
      updater: V,
      generic: Generic.Aux[T#Update, U]
  )(implicit
      drop: Drop.Aux[U, Nats#_1, U2],
      mapper: Mapper.Aux[updater.type, U2, U3],
      toLister: ToTraversable.Aux[U3, List, Option[T#Entity => T#Entity]]
  ): F[Int] =
    ref.modify { map =>
      val updates = (generic
        .to(command)
        .drop(Nat._1)(drop)
        .map(updater)(mapper)
        .toList(toLister): List[Option[T#Entity => T#Entity]]).flatten

      updates match {
        case Nil => (map, 0)
        case nonEmptyList =>
          val updaterF = nonEmptyList.reduce(_ andThen _)
          val id = updateToEntityId.get(command)
          map.get(id).fold((map, 0))(x => (map + (id -> updaterF(x)), 1))
      }
    }
}
