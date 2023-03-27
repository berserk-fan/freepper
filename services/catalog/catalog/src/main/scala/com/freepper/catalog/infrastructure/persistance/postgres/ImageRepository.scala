package com.freepper.catalog.infrastructure.persistance.postgres

import cats.MonadThrow
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.{ConnectionIO, _}
import com.freepper.catalog.domain.image._
import com.freepper.common.domain.error.DbErr
import com.freepper.common.infrastracture.persistance.inmemory.AbstractInMemoryRepository
import com.freepper.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, Queries}
import cats.syntax.functor.toFunctorOps

import java.util.UUID
import cats.effect.{Ref, Sync}
import com.freepper.common.domain.crud.{Crud, RepoOps}

object ImageRepository {
  private object ImageRepositoryImpl extends AbstractPostgresRepository[ImageCrud](ImageQueries) {
    override def findQuery: ImageId => ImageSelector = (id: ImageId) => ImageSelector.IdIs(id)
  }

  object ImageQueries extends Queries[ImageCrud] {
    private def compile(imageSelector: ImageSelector): Fragment = {
      imageSelector match {
        case ImageSelector.All      => fr"1 = 1"
        case ImageSelector.IdIs(id) => fr"im.id = $id"
      }
    }

    override def create(image: CreateImage): List[doobie.Update0] = List({
      val id = image.id
      sql"""
          insert into images (id, src, alt) values ($id, ${image.src}, ${image.alt})
        """.update
    })

    override def find(req: ImageQuery): doobie.Query0[Image] = {
      sql"""
          select id, src, alt
          from images im
          where ${compile(req.selector)}
          ${compileToken(req.page)}
         """.query[Image]
    }

    override def delete(id: ImageId): List[doobie.Update0] = List({
      sql"""
          delete from images im
          where ${compile(ImageSelector.IdIs(id))}
        """.update
    })

    override def update(req: BuzzImageUpdate): List[doobie.Update0] = ???
  }

  private class ImageInMemoryRepository[F[_]: MonadThrow](ref: Ref[F, Map[ImageId, Image]])
      extends AbstractInMemoryRepository[F, ImageCrud](ref) {
    override protected def creator: CreateImage => Image = c => Image(c.id, c.src, c.alt)

    override protected def filter: ImageSelector => Image => Boolean = {
      case ImageSelector.All      => _ => true
      case ImageSelector.IdIs(id) => (x: Image) => x.id == id
    }

    override def update(req: BuzzImageUpdate): F[Int] = ???
  }

  def postgres: ImageRepository[ConnectionIO] = ImageRepositoryImpl
  def inmemory[F[_]: Sync]: F[ImageRepository[F]] = Ref[F].of(Map[ImageId, Image]()).map(new ImageInMemoryRepository[F](_))

}
