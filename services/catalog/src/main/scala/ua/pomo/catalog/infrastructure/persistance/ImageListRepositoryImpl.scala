package ua.pomo.catalog.infrastructure.persistance

import cats.data.{NonEmptyList, OptionT}
import cats.implicits
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, catsSyntaxFlatMapOps}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image.ImageListSelector.IdsIn
import ua.pomo.catalog.domain.image._
import shapeless._

import java.util.UUID

object ImageListRepositoryImpl {
  def apply(): ImageListRepository[ConnectionIO] = {
    new ImageListRepositoryImpl()
  }

  private class ImageListRepositoryImpl() extends ImageListRepository[ConnectionIO] {
    override def create(imageList: ImageList): ConnectionIO[ImageListId] =
      for {
        imageListId <- Queries
          .createImageList(imageList.displayName)
          .withUniqueGeneratedKeys[ImageListId]("id")
        imagesCount <- Queries.createImages.updateMany(imageList.images.zipWithIndex.map { case (x, i) =>
          (x.src, x.alt, imageListId, i)
        })
        _ <- Sync[ConnectionIO].whenA(imagesCount != imageList.images.size) {
          delete(imageListId) >> new Exception("returned ids...").raiseError[ConnectionIO, Unit]
        }
      } yield imageListId

    override def find(id: ImageListId): ConnectionIO[Option[ImageList]] = {
      Queries
        .findImageList(ImageListQuery(PageToken.NonEmpty(2, 0), ImageListSelector.IdsIn(NonEmptyList.of(id))))
        .option
    }

    override def query(query: ImageListQuery): doobie.ConnectionIO[List[ImageList]] = {
      Queries.findImageList(query).to[List]
    }

    override def get(id: ImageListId): ConnectionIO[ImageList] = {
      OptionT(find(id)).getOrElseF(NotFound("imageList", id).raiseError[ConnectionIO, ImageList])
    }

    override def update(req: ImageListUpdate): ConnectionIO[Int] = {
      OptionT(find(req.id))
        .foldF(0.pure[ConnectionIO]) { _ =>
          for {
            updated1 <- Queries.updateImageList(req).fold(0.pure[ConnectionIO])(_.run)
            updated2 <- req.images.fold(0.pure[ConnectionIO]) { images =>
              for {
                deleted <- Queries.deleteImages(req.id).run
                created <- Queries.createImages.updateMany(images.zipWithIndex.map { case (x, i) =>
                  (x.src, x.alt, req.id, i)
                })
              } yield deleted + created
            }
          } yield Math.max(0, Math.min(updated1 + updated2, 1))
        }
    }

    override def delete(id: ImageListId): ConnectionIO[Int] = Queries.delete(id).run
  }

  private[persistance] object Queries {
    private def compile(alias: String, where: ImageListSelector): Fragment = {
      val im = Fragment.const0(alias)
      where match {
        case ImageListSelector.IdsIn(ids) => Fragments.in(fr"$im.id", ids)
      }
    }

    def findImageList(query: ImageListQuery): Query0[ImageList] = {
      implicit val readImages: Get[List[Image]] = jsonAggListJson[Image]

      sql"""
            select il.id, 
                   il.display_name,
                   COALESCE((
                       select json_agg(json_build_object('id', i.id,'src', i.src,'alt', i.alt) ORDER BY i.list_order)
                       from images i
                       where il.id = i.image_list_id
                   ), '[]')
            from image_lists il
            where ${compile("il", query.selector)}
            group by il.id, il.create_time
            order by il.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
        .query[ImageList]
    }

    def createImageList(displayName: ImageListDisplayName): Update0 = {
      sql"""INSERT INTO image_lists (display_name)
            VALUES ($displayName)
         """.update
    }

    def createImages: Update[(ImageSrc, ImageAlt, ImageListId, Int)] = {
      val sql =
        """
           insert into images (src, alt, image_list_id, list_order)
           values (?,?,?,?)
         """
      Update(sql)
    }

    def deleteImages(id: ImageListId): Update0 = {
      sql"""delete from images where image_list_id=$id""".update
    }

    def updateImageList(req: ImageListUpdate): Option[Update0] = {
      object update extends DbUpdaterPoly {
        implicit val a1: Res[ImageListDisplayName] = gen("display_name")
      }
      NonEmptyList
        .fromList((req.displayName :: HNil).map(update).toList.flatten)
        .map { nel =>
          val setFr = Fragments.set(nel.toList: _*)
          sql"""
              update image_lists
              $setFr
              where id=${req.id}
          """.update

        }
    }

    def delete(id: ImageListId): Update0 = {
      sql"""
           delete from image_lists
           where id=$id
         """.update
    }
  }
}
