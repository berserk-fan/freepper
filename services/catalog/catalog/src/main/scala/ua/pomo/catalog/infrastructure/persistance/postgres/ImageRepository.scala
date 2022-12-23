package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import doobie.{ConnectionIO, _}
import ua.pomo.catalog.domain.image._
import ua.pomo.common.domain.error.DbErr
import ua.pomo.common.infrastracture.persistance.postgres.{AbstractPostgresRepository, Queries}

import java.util.UUID

object ImageRepository {
  private object ImageRepositoryImpl extends AbstractPostgresRepository[ImageCrud](ImageQueries) {
    override def idSelector: ImageId => ImageSelector = (id: ImageId) => ImageSelector.IdIs(id)
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

  def postgres: ImageRepository[ConnectionIO] = ImageRepositoryImpl

}
