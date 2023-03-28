package com.freepper.common.infrastracture.persistance.inmemory

import cats.MonadThrow
import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import com.freepper.common.domain.TypeName
import shapeless.*
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import com.freepper.common.domain.crud.*
import com.freepper.common.domain.error.NotFound

import Crud.*

@Deprecated
abstract class AbstractInMemoryRepository[F[_]: MonadThrow, C[_]](ref: Ref[F, Map[C[EntityId], C[Entity]]])(implicit
    updateToEntityId: monocle.Getter[C[Update], C[EntityId]],
    entityToEntityId: monocle.Getter[C[Entity], C[EntityId]],
    show: TypeName[C]
) extends Repository[F, C] {

  override def create(req: C[Create]): F[C[EntityId]]

  override def get(id: C[EntityId]): F[C[Entity]] = {
    find(id).flatMap(
      _.fold(NotFound(show.name, id).raiseError[F, C[Entity]])(_.pure[F])
    )
  }

  override def find(id: C[EntityId]): F[Option[C[Entity]]] = ref.get.map(_.get(id))

  override def findAll(req: C[Crud.Query]): F[List[C[Entity]]]

  override def delete(id: C[EntityId]): F[Int] = ref.modify { map =>
    // qq
    map.get(id).fold((map, 0))(_ => (map - id, 1))
  }

//  protected def updateHelper[U <: HList, U2 <: HList, U3 <: HList, V <: Poly1](
//      command: C[Update],
//      updater: V,
//      generic: Generic.Aux[C[Update], U]
//  )(implicit
//      drop: Drop.Aux[U, Nats#_1, U2],
//      mapper: Mapper.Aux[updater.type, U2, U3],
//      toLister: ToTraversable.Aux[U3, List, Option[C[Entity] => C[Entity]]]
//  ): F[Int] =
//    ref.modify { map =>
//      val updates = (generic
//        .to(command)
//        .drop(Nat._1)(drop)
//        .map(updater)(mapper)
//        .toList(toLister): List[Option[C[Entity] => C[Entity]]]).flatten
//
//      updates match {
//        case Nil => (map, 0)
//        case nonEmptyList =>
//          val updaterF = nonEmptyList.reduce(_ andThen _)
//          val id = updateToEntityId.get(command)
//          map.get(id).fold((map, 0))(x => (map + (id -> updaterF(x)), 1))
//      }
//    }
}
