package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite {
  import CategoryRepositoryImpl._
  override type Impl = CategoryRepository[ConnectionIO]
  override type Res = TestResources
  case class TestResources(db: DbResources, impls: Seq[Impl]) extends HasDbResources with HasImpls
  override val names = Seq("postgres", "inmemory")
  override def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      a <- Resource.pure(CategoryRepositoryImpl())
      b <- Resource.eval(CategoryRepositoryImpl.makeInMemory[ConnectionIO]).mapK[IO](db.xa.trans)
    } yield TestResources(db, Seq(a, b))

  test(s"queries") {
    val uuid = CategoryUUID(UUID.randomUUID())

    check(Queries.findCategory(uuid))
    check(Queries.findCategories)
    check(Queries.deleteCategory(uuid))

    forAll(Generators.Category.update) { update: UpdateCategory =>
      check(Queries.updateCategory(update))
    }

    forAll(Generators.Category.create) { cat: CreateCategory =>
      check(Queries.insertCategory(cat))
    }
  }

  testEachImpl(s"repository api") { impl =>
    forAll(Generators.Category.create) { cat =>
      val found = impl
        .create(cat)
        .flatMap(impl.get)
        .trRun()

      found.displayName should equal(cat.displayName)
      found.description should equal(cat.description)
      found.readableId should equal(cat.readableId)

      val dbId = found.uuid
      val rId = CategoryReadableId("some-id 2")
      val newDisplayName = CategoryDisplayName("qq2")
      impl.update(UpdateCategory(dbId, Some(rId), Some(newDisplayName), None)).trRun()
      impl.get(dbId).trRun().displayName.value should equal(newDisplayName)
      impl.delete(dbId).trRun()
      intercept[Exception] {
        impl.get(dbId).trRun()
      }
    }
  }

  testEachImpl(s"description not empty") { impl =>
    val req = CreateCategory(CategoryReadableId("a"), CategoryDisplayName("b"), CategoryDescription("c"))
    val res = impl.create(req).trRun()
    impl.get(res).trRun().description.value should equal("c")
  }
}
