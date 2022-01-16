package ua.pomo.catalog.shared

import org.scalacheck.Gen
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.category

import java.util.UUID

object Generators {
  object Category {
    val readableId: Gen[CategoryReadableId] = Gen.const(CategoryReadableId("hello"))
    val displayName: Gen[CategoryDisplayName] = Gen.alphaNumStr.map(CategoryDisplayName.apply)
    val description: Gen[CategoryDescription] = Gen.alphaNumStr.map(CategoryDescription.apply)
    val catuuid: CategoryUUID = CategoryUUID(UUID.randomUUID())

    val self: Gen[Category] = for {
      id <- Gen.const(catuuid)
      readableId <- readableId
      displayName <- displayName
      descr <- description
    } yield category.Category(id, readableId, displayName, descr)
  }
}
