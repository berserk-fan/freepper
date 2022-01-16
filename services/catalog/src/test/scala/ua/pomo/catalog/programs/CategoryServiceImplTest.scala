package ua.pomo.catalog.programs

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ua.pomo.catalog.domain.category.{CategoryDisplayName, CategoryReadableId, UpdateCategory}
import ua.pomo.catalog.infrastructure.persistance.CategoryRepositoryImpl
import ua.pomo.catalog.shared.{DbUnitTestSuite, Generators}

class CategoryServiceImplTest extends DbUnitTestSuite with Matchers with ScalaCheckDrivenPropertyChecks {
  val impl = CategoryServiceImpl[IO](xa, CategoryRepositoryImpl())

  test("api") {
    forAll(Generators.Category.self) { cat =>
      val qq = impl.createCategory(cat).unsafeRunSync()
      val found = impl.getCategory(Left(qq.id)).unsafeRunSync()
      found should equal(qq)
      val rId = CategoryReadableId("some_id_2")
      val newDisplayName = CategoryDisplayName("qq2")
      impl.updateCategory(UpdateCategory(Left(qq.id), Some(rId), Some(newDisplayName), None)).unsafeRunSync()
      val updatedCategory = impl.getCategory(Right(rId)).unsafeRunSync()
      updatedCategory.displayName.value should equal (newDisplayName)
      impl.deleteCategory(Left(qq.id)).unsafeRunSync()
      val exception = intercept[Exception] {
        impl.getCategory(Left(qq.id)).unsafeRunSync()
      }
      exception.getMessage should include("not found")
    }
  }
}
