package com.freepper.catalog.infrastructure.persistance.postgres

import cats.{MonadThrow, effect}
import cats.data.NonEmptyList
import cats.effect.Ref
import cats.effect.kernel.Sync
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.util.Get
import doobie.{ConnectionIO, Fragment}
import com.freepper.catalog.domain.image.{Image, ImageAlt, ImageSrc}
import com.freepper.catalog.domain.parameter._
import com.freepper.common.domain.crud
import com.freepper.common.domain.error.DbErr
import com.freepper.common.infrastracture.persistance.inmemory.AbstractInMemoryRepository
import com.freepper.common.infrastracture.persistance.postgres.{
  AbstractPostgresRepository,
  DbUpdaterPoly,
  Queries,
  QueryHelpers
}
import cats.syntax.functor.toFunctorOps

import java.util.UUID

object ParameterListRepository {

  object ParameterListQueries extends Queries[ParameterListCrud] {
    private def createParameters(p: NonEmptyList[CreateParameter], parameterListId: ParameterListId): Fragment = {
      val res = p.zipWithIndex
        .map { case (p, idx) =>
          val id = p.id
          fr"""($id, ${p.displayName}, ${p.description}, ${p.image}, $idx, $parameterListId)"""
        }
        .reduceLeft((a, b) => a ++ fr", " ++ b)
      sql"""INSERT INTO parameters (id, display_name, description, image_id, list_order, parameter_list_id)
            VALUES $res
         """
    }

    override def create(req: CreateParameterList): List[doobie.Update0] = {
      val id = req.id
      val createParameterList = sql"""insert into parameter_lists (id,display_name) values ($id, ${req.displayName})"""
      NonEmptyList
        .fromList(req.parameters)
        .fold {
          List(createParameterList.update)
        } { parameters =>
          val createParameters1 = createParameters(parameters, id)
          List(sql"""
            with subqueries as (
              $createParameters1
            )
            $createParameterList""".update)
        }
    }

    override def delete(id: ParameterListId): List[doobie.Update0] = {
      val del = sql"""delete from parameter_lists where id=$id""".update
      List(del)
    }

    override def find(query: crud.Query[ParameterListSelector]): doobie.Query0[ParameterList] = {
      val limitOffset = compileToken(query.page)
      val where = query.selector match {
        case ParameterListSelector.All      => fr"""1 = 1"""
        case ParameterListSelector.IdIs(id) => fr"""id = $id"""
      }
      implicit val getPL: Get[ParameterList] = readJsonFromView[ParameterList]
      sql"""
        select json
        from parameter_lists_prebuilt
        where $where
        $limitOffset
      """.query[ParameterList]
    }

    object updateParamList extends DbUpdaterPoly {
      implicit val a1: Res[ParamListDisplayName] = gen("display_name")
    }
    def updateParameterList(update: UpdateParameterList): Option[Fragment] = {
      import shapeless._
      val fullUpd = Generic[UpdateParameterList].to(update).reverse.tail.reverse
      QueryHelpers.defaultUpdateRaw(fullUpd, update.id, updateParamList, "parameter_lists")
    }

    override def update(req: UpdateParameterList): List[doobie.Update0] = {
      val updateParameterListClause = updateParameterList(req).map(_.update).toList
      req.parameters.fold {
        updateParameterListClause
      } { params =>
        val deleteParametersClause = sql"""delete from parameters where parameter_list_id=${req.id}""".update
        val createParametersClause = createParameters(NonEmptyList.fromListUnsafe(params), req.id).update
        deleteParametersClause :: createParametersClause :: updateParameterListClause
      }
    }
  }

  private object ParameterListPostgresRepository
      extends AbstractPostgresRepository[ParameterListCrud](ParameterListQueries) {
    override protected def idSelector: ParameterListId => ParameterListSelector = id => ParameterListSelector.IdIs(id)
  }

  private class ParameterListInMemory[F[_]: MonadThrow](ref: Ref[F, Map[ParameterListId, ParameterList]])
      extends AbstractInMemoryRepository[F, ParameterListCrud](ref) {
    override protected def creator: CreateParameterList => ParameterList = cpl =>
      ParameterList(
        cpl.id,
        cpl.displayName,
        cpl.parameters.map(cp =>
          Parameter(cp.id, cp.displayName, cp.image.map(Image(_, ImageSrc(""), ImageAlt(""))), cp.description)
        )
      )

    override protected def filter: ParameterListSelector => ParameterList => Boolean = {
      case ParameterListSelector.All      => _ => true
      case ParameterListSelector.IdIs(id) => x => x.id == id
    }

    override def update(req: UpdateParameterList): F[Int] = ???
  }

  def postgres: ParameterListRepository[ConnectionIO] = ParameterListPostgresRepository
  def inmemory[F[_]: Sync]: F[ParameterListRepository[F]] =
    Ref[F].of(Map[ParameterListId, ParameterList]()).map(new ParameterListInMemory[F](_))
}
