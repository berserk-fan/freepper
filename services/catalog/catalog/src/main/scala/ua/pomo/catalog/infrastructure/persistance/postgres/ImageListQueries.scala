package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.data.NonEmptyList
import doobie._
import doobie.implicits._
import doobie.postgres.implicits.UuidType
import shapeless.HNil
import ua.pomo.catalog.domain.image.{Image, ImageId}
import ua.pomo.catalog.domain.imageList._
import ua.pomo.common.infrastracture.persistance.postgres.{DbUpdaterPoly, Queries}

import java.util.UUID

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

  private def updateImageList(req: ImageListUpdate): Fragment = {
    object update extends DbUpdaterPoly {
      implicit val a1: Res[ImageListDisplayName] = gen("display_name")
    }
    val nel = (req.displayName :: HNil).map(update).toList.flatten
    val setFr = Fragments.set(nel: _*)
    fr"""
        update image_lists
        $setFr
        where id=${req.id}
    """
  }

  override def delete(id: ImageListId): Update0 = {
    fr"""
       delete from image_lists
       where id=$id
     """.update
  }

  private def insertImagesToImageList(id: ImageListId, nonEmptyImages: NonEmptyList[ImageId]): Fragment = {
    val imageAssocVals =
      nonEmptyImages.zipWithIndex
        .map { case (imageId, idx) => Fragments.parentheses(Fragments.values((imageId, id, idx))) }
        .reduce[Fragment] { case (a, b) => fr"(" ++ a ++ fr")" ++ fr"," ++ fr"(" ++ b ++ fr")" }

    fr"""
         INSERT INTO image_list_member (image_id, image_list_id, list_order)
         VALUES $imageAssocVals
      """
  }

  override def create(req: ImageList): (doobie.Update0, ImageListId) = {
    val id = ImageListId(UUID.randomUUID())

    val vals = Fragments.parentheses(Fragments.values((id, req.displayName)))
    val insert = fr"""insert into image_lists values $vals"""

    val sql = NonEmptyList
      .fromList(req.images)
      .fold(insert) { nonEmptyImages =>
        val imageIds = insertImagesToImageList(id, nonEmptyImages.map(_.id))
        fr"""
           WITH queries AS (
             $imageIds
           )
           $insert
        """
      }
      .update

    (sql, id)
  }

  override def update(req: ImageListUpdate): doobie.Update0 = {
    val updateList = updateImageList(req)
    req.images.fold {
      updateList.update
    } { images =>
      val del = fr"DELETE FROM image_lists_member WHERE image_list_id=${req.id}"
      val insertNew = NonEmptyList.fromList(images).map(insertImagesToImageList(req.id, _))
      val subQueries = NonEmptyList
        .of(del)
        .concat(insertNew.toList)
        .toList
        .reduce[Fragment] { case (a, b) => fr"(" ++ a ++ fr"\n," ++ fr"($b)" }
      fr"""
         WITH sub_queries AS (
            $subQueries
         )
         $updateList
      """.update
    }
  }
}
