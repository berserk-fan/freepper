package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, toFunctorOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import shapeless._
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

class ModelRepositoryImpl private (imageListRepository: ImageListRepository[ConnectionIO])
    extends ModelRepository[ConnectionIO] {

  import ModelRepositoryImpl.Queries

  override def create(model: CreateModel): ConnectionIO[ModelId] = {
    Queries.create(model).withUniqueGeneratedKeys[ModelId]("id")
  }

  override def get(id: ModelId): ConnectionIO[Model] = {
    OptionT(find(id))
      .getOrElseF(new Exception(s"model with id $id not found").raiseError[ConnectionIO, Model])
  }

  override def find(id: ModelId): ConnectionIO[Option[Model]] = {
    val res = for {
      imageListId :: modelPart <- OptionT(Queries.getModel(id).option)
      imageList <- OptionT.liftF(imageListRepository.get(imageListId))
    } yield Generic[Model].from(modelPart :+ imageList)
    res.value
  }

  override def findAll(req: FindModel): ConnectionIO[List[Model]] = {
    Queries
      .find(req.categoryUUID, req.page.size, req.page.offset)
      .to[List]
  }

  override def delete(id: ModelId): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
  }

  override def update(req: UpdateModel): ConnectionIO[Int] = {
    Queries.update(req).run
  }
}

object ModelRepositoryImpl {
  def apply(impl: ImageListRepository[ConnectionIO]): ModelRepository[ConnectionIO] = new ModelRepositoryImpl(impl)

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
           insert into models (readable_id, display_name, description, category_id, image_list_id)
           VALUES (${req.readableId}, ${req.displayName}, ${req.description}, ${req.categoryId}, ${req.imageListId})
         """.update
    }

    type GetModelQuery =
      ImageListId :: ModelId :: ModelReadableId :: CategoryUUID :: ModelDisplayName :: ModelDescription :: ModelMinimalPrice :: HNil

    def getModel(modelId: ModelId): Query0[GetModelQuery] = {
      sql"""
           select m.image_list_id, m.id, m.readable_id, m.category_id, m.display_name, m.description, min(COALESCE(p.promo_price_usd, 0))
           from models m left join products p on m.id = p.model_id
           where m.id=$modelId
           group by m.id
         """.query[GetModelQuery]
    }

    def delete(modelId: ModelId): Update0 = {
      sql"""
           delete from models m
           where id=$modelId
         """.update
    }

    def find(categoryUUID: CategoryUUID, limit: Long, offset: Long): Query0[Model] = {
      implicit val readImages: Get[List[Image]] = jsonAggListJson[Image]

      sql"""
        select m.id, m.readable_id, m.category_id, m.display_name, m.description, min(COALESCE(p.promo_price_usd, 0)), il.id, il.display_name,
               case
                 when count(img.id) = 0
                 then '[]'
                 else json_agg((img.id, img.src, img.alt))
               end
        from models m
            join image_lists il on il.id = m.image_list_id
            join products p on m.id = p.model_id
            join image_list_member ilm on m.image_list_id = ilm.image_list_id
            join images img on ilm.image_id = img.id
        where m.category_id = $categoryUUID
        group by m.id, il.id
        order by m.id
        limit $limit
        offset $offset
      """
        .query[
          ModelId :: ModelReadableId :: CategoryUUID :: ModelDisplayName :: ModelDescription :: ModelMinimalPrice ::
            ImageListId :: ImageListDisplayName :: List[Image] :: HNil]
        .map { res =>
          val modelPart_imageListPart = res.split(Nat._6)
          val imageList = Generic[ImageList].from(modelPart_imageListPart._2)
          Generic[Model].from(modelPart_imageListPart._1 :+ imageList)
        }
    }

    def update(req: UpdateModel): Update0 = {
      object updaterOjb extends DbUpdaterPoly {
        implicit val a1: Res[ModelReadableId] = gen("readable_id")
        implicit val a2: Res[ModelDescription] = gen("description")
        implicit val a3: Res[CategoryUUID] = gen("category_id")
        implicit val a4: Res[ModelDisplayName] = gen("display_name")
        implicit val a5: Res[ImageListId] = gen("image_list_id")
      }
      val setFr = Fragments.setOpt(Generic[UpdateModel].to(req).drop(1).map(updaterOjb).toList: _*)
      sql"""
           update models
           $setFr
           where id=${req.id}
         """.update
    }
  }
}
