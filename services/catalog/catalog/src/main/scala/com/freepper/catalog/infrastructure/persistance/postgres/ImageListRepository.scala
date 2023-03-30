package com.freepper.catalog.infrastructure.persistance.postgres

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.effect.{Ref, Sync}
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.UuidType
import monocle.syntax.AppliedLens
import monocle.syntax.all.*
import com.freepper.catalog.domain.image.*
import com.freepper.catalog.domain.imageList.*
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import com.freepper.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, Queries}

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

    private def updateImageList(req: UpdateImageList): Option[Fragment] = ???

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
      val id = req.id

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
    override protected def findQuery: ImageListId => ImageListSelector = (id: ImageListId) =>
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

//    private object updateObj extends InMemoryUpdaterPoly[ImageList] {
//      implicit val a: Res[ImageListDisplayName] = gen(_.focus(_.displayName))
//      implicit val b: Res[List[ImageId]] = gen(x =>
//        AppliedLens[ImageList, List[ImageId]](
//          x,
//          monocle.Lens[ImageList, List[ImageId]](_.images.map(_.id))(value =>
//            imageList => imageList.copy(images = value.map(id => Image(id, ImageSrc(""), ImageAlt(""))))
//          )
//        )
//      )
//    }

    // override def update(req: UpdateImageList): F[Int] = updateHelper(req, updateObj, Generic[UpdateImageList])
    override def update(req: UpdateImageList): F[Int] = ???
  }

  def inmemory[F[_]: Sync]: F[ImageListRepository[F]] = {
    val ref = Ref.of[F, Map[ImageListId, ImageList]](Map())
    Sync[F].map(ref)(new ImageListInMemoryRepositoryImpl[F](_))
  }

  def postgres: ImageListRepository[ConnectionIO] = {
    new ImageListRepositoryImpl()
  }
}
