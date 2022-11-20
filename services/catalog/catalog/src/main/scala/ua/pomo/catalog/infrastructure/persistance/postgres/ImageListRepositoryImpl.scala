package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.data.NonEmptyList
import doobie._
import ua.pomo.catalog.domain.imageList._
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

class ImageListRepositoryImpl private () extends AbstractPostgresRepository[ImageListCrud](ImageListQueries) {
  override protected def idSelector: ImageListId => ImageListSelector = (id: ImageListId) =>
    ImageListSelector.IdsIn(NonEmptyList.of(id))
}

object ImageListRepositoryImpl {
  def apply(): ImageListRepository[ConnectionIO] = {
    new ImageListRepositoryImpl()
  }
}
