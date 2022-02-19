package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.circe.Decoder
import shapeless._
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.domain.parameter._

class ModelRepositoryImpl private () extends ModelRepository[ConnectionIO] {

  import ModelRepositoryImpl.Queries

  override def create(model: CreateModel): ConnectionIO[ModelId] = {
    Queries.create(model).withUniqueGeneratedKeys[ModelId]("id")
  }

  override def get(id: ModelId): ConnectionIO[Model] = {
    OptionT(find(id))
      .getOrElseF(new Exception(s"model with id $id not found").raiseError[ConnectionIO, Model])
  }

  override def find(id: ModelId): ConnectionIO[Option[Model]] = {
    OptionT(Queries.find(ModelQuery(ModelSelector.IdIs(id), PageToken.NonEmpty(2, 0))).option).value
  }

  override def findAll(req: ModelQuery): ConnectionIO[List[Model]] = {
    Queries.find(req).to[List]
  }

  override def delete(id: ModelId): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
  }

  override def update(req: UpdateModel): ConnectionIO[Int] = {
    Queries.update(req).run
  }
}

object ModelRepositoryImpl {
  def apply(): ModelRepository[ConnectionIO] = new ModelRepositoryImpl()

  def makeInMemory[F[_]: Sync]: F[ModelRepository[F]] = {
    Ref[F]
      .of(Map[ModelId, Model]())
      .map(
        new InMemoryModelRepositoryImpl[F](_)
      )
  }

  private[persistance] object Queries {
    private implicit val readModelMinimalPrice: Read[ModelMinimalPrice] =
      Read[Double].map(x => ModelMinimalPrice(Money(x, USD)))

    def create(req: CreateModel): Update0 = {
      sql"""
           insert into models (readable_id, display_name, description, category_id, image_list_id, parameter_list_ids)
           VALUES (${req.readableId}, ${req.displayName}, ${req.description}, ${req.categoryId}, ${req.imageListId}, ${req.parameterListIds
        .map(_.value)})
         """.update
    }

    type GetModelQuery =
      ImageListId :: ModelId :: ModelReadableId :: CategoryUUID :: ModelDisplayName :: ModelDescription :: ModelMinimalPrice :: List[
        ParameterList] :: HNil

    def delete(modelId: ModelId): Update0 = {
      sql"""
           delete from models m
           where id=$modelId
         """.update
    }

    private def compileWhere(alias: String, where: ModelSelector): Fragment = {
      val p = Fragment.const0(alias)
      where match {
        case ModelSelector.All              => fr"1 = 1"
        case ModelSelector.IdIs(id)         => fr"$p.id = $id"
        case ModelSelector.CategoryIdIs(id) => fr"$p.category_id = $id"
      }
    }

    def find(query: ModelQuery): Query0[Model] = {
      implicit val readImages: Get[List[Image]] = jsonAggListJson[Image]
      implicit val readParameterList: Get[List[ParameterList]] = jsonAggListJson[ParameterList]

      sql"""
        select m.id, 
               m.readable_id, 
               m.category_id, 
               m.display_name, 
               m.description,
               COALESCE((
                  select min(COALESCE(p.promo_price_usd, p.price_usd, 0))
                  from products p
                  where p.model_id = m.id
               ), 0), 
               COALESCE( (
                    select json_agg(json_build_object(
                        'id', pl.id, 
                        'displayName', pl.display_name, 
                        'parameters', COALESCE( (
                            select json_agg(json_build_object(
                                'id', par.id, 
                                'displayName', par.display_name ,
                                'image', (select json_build_object('src', img.src, 'alt', img.alt)
                                          from images img
                                          where image_id = par.image_id)) ORDER BY par.list_order)
                            from parameters par where par.parameter_list_id = parameter_list_id), '[]'))
                        )
                    from parameter_lists pl join unnest(m.parameter_list_ids) parameter_list_id on pl.id = parameter_list_id
               ), '[]'),
               il.id, il.display_name,
               COALESCE((
                   select json_agg(json_build_object('src', img.src, 'alt', img.alt) ORDER BY img.list_order)
                   from images img
                   where img.image_list_id = il.id
               ), '[]')
        from models m left join image_lists il on il.id = m.image_list_id
        where ${compileWhere("m", query.selector)}
        group by m.id, il.id
        order by m.create_time
        limit ${query.page.size}
        offset ${query.page.offset}
      """
        .query[Model]
    }

    def update(req: UpdateModel): Update0 = {
      object updaterObj extends DbUpdaterPoly {
        implicit val a1: Res[ModelReadableId] = gen("readable_id")
        implicit val a2: Res[ModelDescription] = gen("description")
        implicit val a3: Res[CategoryUUID] = gen("category_id")
        implicit val a4: Res[ModelDisplayName] = gen("display_name")
        implicit val a5: Res[ImageListId] = gen("image_list_id")
      }
      val setFr = Fragments.setOpt(Generic[UpdateModel].to(req).drop(1).map(updaterObj).toList: _*)
      sql"""
           update models
           $setFr
           where id=${req.id}
         """.update
    }
  }
}
