package ua.pomo.catalog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.{catsSyntaxTuple2Semigroupal, toTraverseOps}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ua.pomo.catalog.api.{CreateModelRequest, ListModelsRequest}
import ua.pomo.catalog.app.{CatalogImpl, CategoryName, Converters, ModelsName}
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
    Generators.Category.self.sample.get

    Generators.Model.self.toLazyList.map(model =>
      CreateModelRequest(ModelsName(Some(CategoryId(model.categoryId))).toNameString, Some(Converters.toApi(model)))
    ).take(10).traverse(impl.createModel(_, null)).unsafeRunSync()

    val parent = CategoryName(CategoryId(CategoryUUID(UUID.randomUUID())))

    noException should be thrownBy impl.listModels(ListModelsRequest(parent.toNameString, 0, ""), null).unsafeRunSync()
  }
}
