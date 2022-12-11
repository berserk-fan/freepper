package ua.pomo.common.infrastracture.persistance.inmemory

import cats.MonadThrow
import cats.effect.Ref
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import doobie.Update
import shapeless._
import shapeless.ops.hlist.{Drop, Mapper, ToTraversable}
import ua.pomo.common.domain.repository.{CrudOps, _}

abstract class AbstractInMemoryRepository[F[_]: MonadThrow, T <: Crud](ref: Ref[F, Map[T#EntityId, T#Entity]])(implicit
    crudOps: CrudOps[T]
) extends Repository[F, T] {
  protected def creator: T#Create => T#Entity
  protected def filter: T#Selector => T#Entity => Boolean

  override def create(req: T#Create): F[T#EntityId] = ref.modify { map =>
    val entity = creator(req)
    val id = crudOps.getIdEntity(entity)
    val newMap = map + (id -> entity)
    (newMap, id)
  }

  override def get(id: T#EntityId): F[T#Entity] = find(id).flatMap(
    _.fold(new Exception(s"${crudOps.entityDisplayName} with id $id not found").raiseError[F, T#Entity])(_.pure[F])
  )

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
    map.get(id).fold((map, 0))(x => (map - id, 1))
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
      val updaterF =
        (generic
          .to(command)
          .drop(Nat._1)(drop)
          .map(updater)(mapper)
          .toList(toLister): List[Option[T#Entity => T#Entity]]).flatten
          .reduce(_ andThen _)
      val id = crudOps.getIdUpdate(command)
      map.get(id).fold((map, 0))(x => (map + (id -> updaterF(x)), 1))
    }
}
