package ua.pomo.catalog.infrastructure.persistance.postgres

import doobie._
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import ua.pomo.catalog.domain.image._
import ua.pomo.common.domain.repository.Query
import ua.pomo.common.infrastracture.persistance.postgres.Queries

import java.util.UUID

object ImageQueries extends Queries[ImageCrud] {
  private def compile(imageSelector: ImageSelector): Fragment = {
    imageSelector match {
      case ImageSelector.All      => fr"1 = 1"
      case ImageSelector.IdIs(id) => fr"im.id = $id"
    }
  }

  override def create(image: CreateImageMetadata): (doobie.Update0, ImageId) = {
    val id = ImageId(UUID.randomUUID())
    val sql = sql"""
        insert into images (id, src, alt) values ($id, ${image.src}, ${image.alt})
      """.update
    (sql, id)
  }

  override def find(req: Query[ImageSelector]): doobie.Query0[Image] = {
    sql"""
        select id, src, alt
        from images im
        where ${compile(req.selector)}
        ${compileToken(req.page)}
       """.query[Image]
  }

  override def delete(id: ImageId): Update0 = {
    sql"""
        delete from images im
        where ${compile(ImageSelector.IdIs(id))}
      """.update
  }

  override def update(req: BuzzImageUpdate): Option[doobie.Update0] = ???
}
