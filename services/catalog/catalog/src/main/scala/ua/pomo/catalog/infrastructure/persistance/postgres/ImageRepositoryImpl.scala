package ua.pomo.catalog.infrastructure.persistance.postgres

import ua.pomo.catalog.domain.image._
import ua.pomo.common.infrastracture.persistance.postgres.AbstractPostgresRepository

object ImageRepositoryImpl extends AbstractPostgresRepository[ImageCrud](ImageQueries) {
  override def idSelector: ImageId => ImageSelector = (id: ImageId) => ImageSelector.IdIs(id)
}
