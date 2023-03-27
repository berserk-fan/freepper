package com.freepper.common.infrastracture.persistance.inmemory

import cats.MonadThrow
import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import com.freepper.common.domain.TypeName
import shapeless.*
import shapeless.ops.hlist.{Drop, MapFolder, Mapper, ToTraversable}
import com.freepper.common.domain.crud.*
import com.freepper.common.domain.error.NotFound

import Crud._

abstract class AbstractInMemoryRepositoryV2[F[_]: MonadThrow, C[_]](
    ref: Ref[F, Map[C[EntityId], C[Entity]]],
    entityToEntityId: monocle.Getter[C[Entity], C[EntityId]]
)(implicit show: TypeName[C])
    extends Repository[F, C] {

  override def get(id: C[EntityId]): F[C[Entity]] = {
    find(id).flatMap(
      _.fold(NotFound(show.name, id).raiseError[F, C[Entity]])(_.pure[F])
    )
  }

  override def find(id: C[EntityId]): F[Option[C[Entity]]] = ref.get.map(_.get(id))

  override def findAll(req: C[Crud.Query]): F[List[C[Entity]]]

  override def delete(id: C[EntityId]): F[Int] = ref.modify { map =>
    map.get(id).fold((map, 0))(_ => (map - id, 1))
  }
}
