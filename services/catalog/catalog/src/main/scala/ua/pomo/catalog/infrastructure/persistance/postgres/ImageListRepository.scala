package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.effect.{Ref, Sync}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits.UuidType
import monocle.syntax.AppliedLens
import monocle.syntax.all._
import shapeless.{HNil, _}
import ua.pomo.catalog.domain.image.{Image, ImageId, _}
import ua.pomo.catalog.domain.imageList.{ImageListCrud, _}
import ua.pomo.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import ua.pomo.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, DbUpdaterPoly, Queries}

import java.util.UUID

object ImageListRepository {

  object ImageListQueries extends Queries[ImageListCrud] {
    private def compile(alias: String, where: ImageListSelector): Fragment = {
      val im = Fragment.const0(alias)
      where match {
        case ImageListSelector.IdsIn(ids) => Fragments.in(fr"$im.id", ids)
        case ImageListSelector.All        => fr"1 = 1"
      }
    }

    override def find(query: ImageListQuery): Query0[ImageList] = {
      implicit val readImages: Get[List[Image]] = jsonAggListJson[Image]

      sql"""
              select il.id, 
                     il.display_name,
                     ${DeprecatedMethods.jsonList("il.id")}
              from image_lists il
              where ${compile("il", query.selector)}
              group by il.id, il.create_time
              order by il.create_time
              limit ${query.page.size}
              offset ${query.page.offset}
           """
        .query[ImageList]
    }

    private def updateImageList(req: UpdateImageList): Option[Fragment] = {
      object update extends DbUpdaterPoly {
        implicit val a1: Res[ImageListDisplayName] = gen("display_name")
      }
      val nel = (req.displayName :: HNil).map(update).toList.flatten
      if (nel.isEmpty) {
        None
      } else {
        val setFr = Fragments.set(nel: _*)
        val res =
          fr"""
           update image_lists
           $setFr
           where id=${req.id}
        """
        Some(res)
      }
    }

    override def delete(id: ImageListId): List[Update0] = List({
      fr"""
         delete from image_lists
         where id=$id
       """.update
    })

    private def insertImagesToImageList(id: ImageListId, nonEmptyImages: NonEmptyList[ImageId]): Fragment = {
      val imageAssocVals =
        nonEmptyImages.zipWithIndex
          .map { case (imageId, idx) => fr"""($imageId, $id, $idx)""" }
          .reduceLeft((a, b) => a ++ fr"," ++ b)

      fr"""
           INSERT INTO image_list_member (image_id, image_list_id, list_order)
           VALUES $imageAssocVals
        """
    }

    override def create(req: CreateImageList): List[doobie.Update0] = List({
      val id = req.id.getOrElse(ImageListId(UUID.randomUUID()))

      val vals = fr"VALUES ($id, ${req.displayName})"
      val insert = fr"""insert into image_lists $vals"""

      NonEmptyList
        .fromList(req.images)
        .fold(insert) { nonEmptyImages =>
          val imageIds = insertImagesToImageList(id, nonEmptyImages)
          fr"""
             WITH queries AS (
               $imageIds
             )
             $insert
          """
        }
        .update
    })

    override def update(req: UpdateImageList): List[doobie.Update0] = {
      if (req.productIterator.forall(x => x == req.id || x == None)) {
        Nil
      } else {
        val updateList = updateImageList(req).map(_.update)
        req.images.fold {
          updateList.toList
        } { images =>
          val deleteImages = fr"DELETE FROM image_list_member WHERE image_list_id=${req.id}".update
          val insertImages = NonEmptyList.fromList(images).map(insertImagesToImageList(req.id, _).update)
          deleteImages :: updateList.toList ::: insertImages.toList
        }
      }
    }
  }

  private class ImageListRepositoryImpl() extends AbstractPostgresRepository[ImageListCrud](ImageListQueries) {
    override protected def idSelector: ImageListId => ImageListSelector = (id: ImageListId) =>
      ImageListSelector.IdsIn(NonEmptyList.of(id))
  }

  private class ImageListInMemoryRepositoryImpl[F[_]: MonadThrow](mapRef: Ref[F, Map[ImageListId, ImageList]])
      extends AbstractInMemoryRepository[F, ImageListCrud](mapRef) {
    override protected def creator: CreateImageList => ImageList = cil =>
      ImageList(ImageListId(UUID.randomUUID()), cil.displayName, cil.images.map(Image(_, ImageSrc(""), ImageAlt(""))))

    override protected def filter: ImageListSelector => ImageList => Boolean = {
      case ImageListSelector.IdsIn(ids) => (i: ImageList) => ids.toList.toSet.contains(i.id)
      case ImageListSelector.All        => (_: ImageList) => true
    }

    private object updateObj extends InMemoryUpdaterPoly[ImageList] {
      implicit val a: Res[ImageListDisplayName] = gen(_.focus(_.displayName))
      implicit val b: Res[List[ImageId]] = gen(x =>
        AppliedLens[ImageList, List[ImageId]](
          x,
          monocle.Lens[ImageList, List[ImageId]](_.images.map(_.id))(value =>
            imageList => imageList.copy(images = value.map(id => Image(id, ImageSrc(""), ImageAlt(""))))
          )
        )
      )
    }

    override def update(req: UpdateImageList): F[Int] = updateHelper(req, updateObj, Generic[UpdateImageList])
  }

  def inmemory[F[_]: Sync]: F[ImageListRepository[F]] = {
    val ref = Ref.of[F, Map[ImageListId, ImageList]](Map())
    Sync[F].map(ref)(new ImageListInMemoryRepositoryImpl[F](_))
  }

  def postgres: ImageListRepository[ConnectionIO] = {
    new ImageListRepositoryImpl()
  }
}
