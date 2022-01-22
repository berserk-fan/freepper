package ua.pomo.catalog.infrastructure.persistance

import cats.MonadThrow
import cats.data.OptionT
import cats.implicits.catsSyntaxApplicativeId
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
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
      val res = for {
        (id, displayName) <- OptionT(Queries.selectImageList(id).option)
        images <- OptionT.liftF(Queries.selectImages(id).to[List])
      } yield ImageList(id, displayName, images)
      res.value
    }

    override def get(id: ImageListId): ConnectionIO[ImageList] =
      for {
        (id, displayName) <- Queries.selectImageList(id).unique
        images <- Queries.selectImages(id).to[List]
      } yield ImageList(id, displayName, images)

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
    def selectImageList(id: ImageListId): Query0[(ImageListId, ImageListDisplayName)] = {
      sql"""select id, display_name
            from image_lists
            where id=$id"""
        .query[(ImageListId, ImageListDisplayName)]
    }

    def selectImages(id: ImageListId): Query0[Image] = {
      sql"""select im.id, im.src, im.alt
            from images im join image_list_member ilm on im.id = ilm.image_id
            where ilm.image_list_id=$id
         """
        .query[Image]
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
