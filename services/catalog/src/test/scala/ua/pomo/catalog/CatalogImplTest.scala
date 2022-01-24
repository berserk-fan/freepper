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
    Generators.Model.self
      .map(_.copy(categoryId = categoryId))
      .toLazyList
      .map(model =>
        CreateModelRequest(ModelsName(Some(CategoryId(model.categoryId))).toNameString, Some(Converters.toApi(model))))
      .take(10)
      .traverse(impl.createModel(_, null))
      .unsafeRunSync()

    val parent = ModelsName(Some(CategoryId(categoryId)))

    noException should be thrownBy impl.listModels(ListModelsRequest(parent.toNameString, 0, ""), null).unsafeRunSync()
    impl.listModels(ListModelsRequest(parent.toNameString, 5, ""), null).unsafeRunSync().models.length should equal(5)
  }
}
