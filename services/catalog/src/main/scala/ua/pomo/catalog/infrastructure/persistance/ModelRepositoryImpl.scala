package ua.pomo.catalog.infrastructure.persistance

import cats.data.OptionT
import cats.effect.{Ref, Sync}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFunctorOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import io.circe.Json
import shapeless._
import squants.market.{Money, USD}
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category.CategoryUUID
import ua.pomo.catalog.domain.image._
import ua.pomo.catalog.domain.model._

class ModelRepositoryImpl private (imageListRepository: ImageListRepository[ConnectionIO])
    extends ModelRepository[ConnectionIO] {

  import ModelRepositoryImpl.Queries

  override def create(model: CreateModel): ConnectionIO[ModelUUID] = {
    Queries.create(model).withUniqueGeneratedKeys[ModelUUID]("id")
  }

  override def get(id: ModelUUID): ConnectionIO[Model] = {
    OptionT(find(id))
      .getOrElseF(new Exception(s"model with id $id not found").raiseError[ConnectionIO, Model])
  }

  override def find(id: ModelUUID): ConnectionIO[Option[Model]] = {
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

  override def delete(id: ModelUUID): ConnectionIO[Unit] = {
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
      .of(Map[ModelUUID, Model]())
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
      ImageListId :: ModelUUID :: ModelReadableId :: CategoryUUID :: ModelDisplayName :: ModelDescription :: ModelMinimalPrice :: HNil

    def getModel(modelId: ModelUUID): Query0[GetModelQuery] = {
      sql"""
           select m.image_list_id, m.id, m.readable_id, m.category_id, m.display_name, m.description, min(COALESCE(p.promo_price_usd, 0))
           from models m left join products p on m.id = p.model_id
           where m.id=$modelId
           group by m.id
         """.query[GetModelQuery]
    }

    def delete(modelId: ModelUUID): Update0 = {
      sql"""
           delete from models m
           where id=$modelId
         """.update
    }

    private type FindQuery =
      ModelUUID :: ModelReadableId :: CategoryUUID :: ModelDisplayName :: ModelDescription :: ModelMinimalPrice ::
        ImageListId :: ImageListDisplayName :: List[Image] :: HNil

    def find(categoryUUID: CategoryUUID, limit: Long, offset: Long): Query0[Model] = {
      implicit val readImages: Read[List[Image]] = Read[Json].map { _ =>
        ???
      }

      sql"""
        select m.id, m.readable_id, m.category_id, m.display_name, m.description, min(COALESCE(p.promo_price_usd, 0)), il.id, il.display_name, json_agg((img.id, img.src, img.alt))
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
        .query[FindQuery]
        .map { res =>
          val modelPart_imageListPart = res.split(Nat._6)
          val imageList = Generic[ImageList].from(modelPart_imageListPart._2)
          Generic[Model].from(modelPart_imageListPart._1 :+ imageList)
        }
    }

    def update(req: UpdateModel): Update0 = {
      val setFr = Fragments.setOpt(
        req.readableId.map(x => fr"readable_id=$x"),
        req.description.map(x => fr"description=$x"),
        req.categoryId.map(x => fr"category_id=$x"),
        req.displayName.map(x => fr"display_name=$x"),
        req.imageListId.map(x => fr"image_list_id=$x"),
      )
      sql"""
           update models
           $setFr
         """.update
    }
  }
}
