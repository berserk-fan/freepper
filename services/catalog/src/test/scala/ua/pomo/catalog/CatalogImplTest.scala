package ua.pomo.catalog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.{catsSyntaxTuple2Semigroupal, toTraverseOps}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{CreateCategoryRequest, CreateModelRequest, ListModelsRequest}
import ua.pomo.catalog.app.{CatalogImpl, Converters, ModelsName}
import ua.pomo.catalog.app.programs.{CategoryServiceImpl, ModelServiceImpl}
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.domain.model._
import ua.pomo.catalog.shared.Generators
import Generators.ToLazyListOps

import java.util.UUID

class CatalogImplTest extends AnyFunSuite with BeforeAndAfter with Matchers {
  val impl = (
    CategoryServiceImpl.makeInMemory[IO],
    ModelServiceImpl.makeInMemory[IO]
  ).mapN(CatalogImpl(_, _)).unsafeRunSync()

  test("list") {
    val category = Generators.Category.self.sample.get
    val categoryIdStr = impl
      .createCategory(CreateCategoryRequest("categories", Some(Converters.toApi(category))), null)
      .unsafeRunSync()
      .id

    val categoryId = CategoryUUID(UUID.fromString(categoryIdStr))
    val totalModels = 10
    Generators.Model.self
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .map(model =>
        CreateModelRequest(ModelsName(Some(model.categoryId)).toNameString, Some(Converters.toApi(model))))
      .take(totalModels)
      .traverse(impl.createModel(_, null))
      .unsafeRunSync()

    val parent = ModelsName(Some(categoryId))

    noException should be thrownBy impl.listModels(ListModelsRequest(parent.toNameString, 0, ""), null).unsafeRunSync()
    val pageLength = 4
    val page1 = impl.listModels(ListModelsRequest(parent.toNameString, pageLength, ""), null).unsafeRunSync()
    page1.models.length should equal(pageLength)
    val page2 = impl.listModels(ListModelsRequest(parent.toNameString, pageLength, page1.nextPageToken), null).unsafeRunSync()
    page2.models.length should equal(pageLength)
    val page3 = impl.listModels(ListModelsRequest(parent.toNameString, pageLength, page2.nextPageToken), null).unsafeRunSync()
    page3.models.length should equal(2)


  }
}
