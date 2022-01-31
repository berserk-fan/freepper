package ua.pomo.catalog.infrastructure.persistance

import cats.MonadThrow
import cats.data.{NonEmptyList, OptionT}
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.image.ImageListSelector.IdsIn
import ua.pomo.catalog.domain.image._

import java.util.UUID

object ImageListRepositoryImpl {
  def apply(): ImageListRepository[ConnectionIO] = {
    new ImageListRepositoryImpl()
  }

  private class ImageListRepositoryImpl() extends ImageListRepository[ConnectionIO] {
    override def create(imageList: ImageList): ConnectionIO[ImageListId] =
      for {
        generatedId <- Queries
          .createImageList(imageList.displayName)
          .withUniqueGeneratedKeys[ImageListId]("id")
        imagesIds <- Queries.upsertImage.updateManyWithGeneratedKeys[ImageId]("id")(imageList.images).compile.toList
        _ <- MonadThrow[ConnectionIO].raiseWhen(imagesIds.size != imageList.images.size)(
          new Exception("returned ids..."))
        _ <- Queries.createMembership.updateMany(imagesIds.map((generatedId, _)))
      } yield generatedId

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

    def updateImages(imageListId: ImageListId, imageListImages: List[Image]): ConnectionIO[Unit] =
      for {
        _ <- Queries.clearMembership(imageListId).run
        ids <- Queries.upsertImage.updateManyWithGeneratedKeys[ImageId]("id")(imageListImages).compile.toList
        _ <- Queries.createMembership.updateMany(ids.map(id => (imageListId, id)))
      } yield ()

    override def update(req: ImageListUpdate): ConnectionIO[Int] = {
      OptionT(find(req.id))
        .foldF(0.pure[ConnectionIO]) { _ =>
          for {
            updated <- req.displayName.fold(0.pure[ConnectionIO]) {
              Queries.updateImageList(req.id, _).run
            }
            _ <- req.images.fold(().pure[ConnectionIO])(updateImages(req.id, _))
          } yield updated
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
                   case 
                       when count(i.id) = 0 
                       then '[]'
                       else json_agg(json_build_object('id', i.id,'src', i.src,'alt', i.alt)) 
                   end
            from image_lists il
                left join image_list_member ilm on il.id = ilm.image_list_id
                left join images i on ilm.image_id = i.id
            where ${compile("il", query.selector)}
            group by il.id
            order by il.create_time
            limit ${query.pageToken.size}
            offset ${query.pageToken.offset}
         """
        .query[ImageList]
    }

    def createImageList(displayName: ImageListDisplayName): Update0 = {
      sql"""insert into image_lists (display_name)
            VALUES ($displayName)
         """.update
    }

    def upsertImage: Update[Image] = {
      val sql =
        """
           insert into images (id, src, alt)
           values (?,?,?)
           on conflict (src) DO UPDATE SET alt = EXCLUDED.alt
         """
      Update[Image](sql)
    }

    def createMembership: Update[(ImageListId, ImageId)] = {
      implicit val w: Write[(ImageListId, ImageId)] =
        Write[(UUID, UUID)].contramap((v: (ImageListId, ImageId)) => (v._1.uuid, v._2.uuid))
      val sql =
        """
         insert into image_list_member (image_list_id, image_id)
         values (?, ?)
         on conflict do nothing
      """
      Update[(ImageListId, ImageId)](sql)
    }

    def updateImageList(id: ImageListId, displayName: ImageListDisplayName): Update0 = {
      val setFr = Fragments.set(fr"display_name = $displayName")
      sql"""
        update image_lists
        $setFr
        where id=$id
        """.update
    }

    def clearMembership(id: ImageListId): Update0 = {
      sql"""
           delete from image_list_member
           where image_list_id=$id
         """.update
    }

    def delete(id: ImageListId): Update0 = {
      sql"""
           delete from image_lists
           where id=$id
         """.update
    }
  }
}
