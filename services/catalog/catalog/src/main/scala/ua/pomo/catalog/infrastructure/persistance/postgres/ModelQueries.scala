package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.data.NonEmptyList
import doobie._
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import shapeless.Generic
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image.Image
import ua.pomo.catalog.domain.imageList.ImageListId
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter.ParameterList
import ua.pomo.common.domain.error.DbErr
import ua.pomo.common.infrastracture.persistance.postgres.{DbUpdaterPoly, Queries, QueriesHelpers}

object ModelQueries extends Queries[ModelCrud] {
  private implicit val readModelMinimalPrice: Read[ModelMinimalPrice] =
    Read[Double].map(x => ModelMinimalPrice(Money(x, USD)))

  override def create(req: CreateModel): List[doobie.Update0] = List({
    val modelId = req.id.getOrElse(throw DbErr("No Id(")).value
    val modelsInsert =
      sql"""
          INSERT INTO models (id, readable_id, display_name, description, category_id, image_list_id)
           VALUES ($modelId,
                   ${req.readableId},
                   ${req.displayName},
                   ${req.description},
                   ${req.categoryId},
                   ${req.imageListId}
                 )
           """

    val res = NonEmptyList.fromList(req.parameterListIds).fold(modelsInsert) { paramsNonEmpty =>
      val mplValues = Fragments.values(paramsNonEmpty.map((modelId, _)))
      sql"""
           WITH modelsInsert as (
             $modelsInsert
           )
           INSERT INTO model_parameter_lists (model_id, parameter_list_id) $mplValues
          """
    }

    res.update
  })

  override def delete(id: ModelId): List[doobie.Update0] = List {
    sql"""
           delete from models m
           where id=$id
         """.update
  }

  private def compileWhere(modelsTable: String, where: ModelSelector): Fragment = {
    val models = Fragment.const0(modelsTable)
    where match {
      case ModelSelector.All              => fr"1 = 1"
      case ModelSelector.IdIs(id)         => fr"$models.id = $id"
      case ModelSelector.CategoryIdIs(id) => fr"$models.category_id = $id"
    }
  }

  override def find(query: ModelQuery): Query0[Model] = {
    implicit val readImages: Get[List[Image]] = jsonAggListJson[Image]
    implicit val readParameterList: Get[List[ParameterList]] = jsonAggListJson[ParameterList]

    sql"""
        select m.id,
               m.readable_id,
               m.category_id,
               c.readable_id,
               m.display_name,
               m.description,
               COALESCE((
                  select min(COALESCE(p.promo_price, p.price, 0))
                  from products p
                  where p.model_id = m.id
               ), 0),
               ${ParameterListRepository.Queries.jsonList("m.id")},
               il.id, il.display_name,
               ${DeprecatedMethods.jsonList("il.id")}
        from models m
            left join categories c on c.id = m.category_id
            left join image_lists il on il.id = m.image_list_id
        where ${compileWhere("m", query.selector)}
        group by m.id, c.readable_id, il.id
        order by m.create_time desc
        limit ${query.page.size}
        offset ${query.page.offset}
      """
      .query[Model]
  }

  object updaterObj extends DbUpdaterPoly {
    implicit val a1: Res[ModelReadableId] = gen("readable_id")
    implicit val a2: Res[ModelDescription] = gen("description")
    implicit val a3: Res[CategoryUUID] = gen("category_id")
    implicit val a4: Res[ModelDisplayName] = gen("display_name")
    implicit val a5: Res[ImageListId] = gen("image_list_id")
  }

  override def update(req: UpdateModel): List[doobie.Update0] = {
    QueriesHelpers.updateQHelper(Generic[UpdateModel].to(req), req.id, updaterObj, "model").map(_.update).toList
  }
}
