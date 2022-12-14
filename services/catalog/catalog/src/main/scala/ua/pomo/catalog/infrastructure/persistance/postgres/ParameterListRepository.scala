package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.data.NonEmptyList
import doobie.Fragment
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.util.Get
import doobie.ConnectionIO
import io.circe.Json
import ua.pomo.catalog.domain.parameter._
import ua.pomo.common.domain.error.DbErr
import ua.pomo.common.domain.repository
import ua.pomo.common.infrastracture.persistance.postgres.{
  AbstractPostgresRepository,
  DbUpdaterPoly,
  Queries,
  QueriesHelpers
}

object ParameterListRepository {
  object Queries {
    def jsonList(modelId: String): Fragment = {
      val imagesQuery = DeprecatedMethods.json("par.image_id")

      fr"""COALESCE( (
             select json_agg(json_build_object(
                 'id', pl.id, 
                 'displayName', pl.display_name, 
                 'parameters', COALESCE( (
                     select json_agg(json_build_object(
                         'id', par.id,
                         'displayName', par.display_name ,
                         'image', $imagesQuery) ORDER BY par.list_order)
                     from parameters par where par.parameter_list_id = pl.id), '[]'))
                 )
             from parameter_lists pl join model_parameter_lists mpl on pl.id = mpl.parameter_list_id 
             WHERE mpl.model_id = ${Fragment.const0(modelId)}
        ), '[]')"""
    }
  }

  object ParameterListQueries extends Queries[ParameterListCrud] {
    private def createParameters(p: NonEmptyList[CreateParameter], parameterListId: ParameterListId): Fragment = {
      val res = p.zipWithIndex
        .map { case (p, idx) =>
          val id = p.id.getOrElse(throw DbErr("No parameter id in create"))
          fr"""($id, ${p.displayName}, ${p.description}, ${p.image}, $idx, $parameterListId)"""
        }
        .reduceLeft((a, b) => a ++ fr", " ++ b)
      sql"""INSERT INTO parameters (id, display_name, description, image_id, list_order, parameter_list_id)
            VALUES $res
         """
    }

    override def create(req: CreateParameterList): List[doobie.Update0] = {
      val id = req.id.getOrElse(throw DbErr("No Id found."))
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

    override def find(query: repository.Query[ParameterListSelector]): doobie.Query0[ParameterList] = {
      val limitOffset = compileToken(query.page)
      val where = query.selector match {
        case ParameterListSelector.All      => fr"""1 = 1"""
        case ParameterListSelector.IdIs(id) => fr"""id = $id"""
      }
      implicit val getPL: Get[ParameterList] = doobie.Get[Json].temap(_.as[ParameterList].left.map(_.getMessage()))
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
      QueriesHelpers.updateQHelper(fullUpd, update.id, updateParamList, "parameter_lists")
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

  def postgres: ParameterListRepository[ConnectionIO] = ParameterListPostgresRepository
}
