package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared.{DbResources, DbUnitTestSuite, Generators, HasDbResources, Resources}

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution {
  import CategoryRepositoryImpl._
  override type Impl = CategoryRepository[ConnectionIO]
  override type Res = TestResources
  override val resourcePerTest: Boolean = true

  case class TestResources(db: DbResources, impls: Seq[Impl]) extends HasDbResources with HasImpls
  override val names = Seq("postgres", "inmemory")
  override def resource: Resource[IO, Res] =
    for {
      db <- Resources.dbTest
      a <- Resource.pure(CategoryRepositoryImpl())
      b <- Resource.eval(CategoryRepositoryImpl.makeInMemory[ConnectionIO]).mapK[IO](db.xa.trans)
    } yield TestResources(db, Seq(a, b))

  test(s"queries") {
    val uuid = CategoryId(UUID.randomUUID())

    check(Queries.single(uuid))
    check(Queries.query(CategoryQuery(CategorySelector.All, PageToken.Two)))
    check(Queries.query(CategoryQuery(CategorySelector.All, PageToken.NonEmpty(10, 10))))
    check(Queries.query(CategoryQuery(CategorySelector.IdIs(uuid), PageToken.NonEmpty(10, 10))))
    check(Queries.delete(uuid))

    forAll(Generators.Category.update) { update: UpdateCategory =>
      check(Queries.update(update))
    }

    forAll(Generators.Category.create) { cat: CreateCategory =>
      check(Queries.insert(cat))
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

      val dbId = found.id
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
