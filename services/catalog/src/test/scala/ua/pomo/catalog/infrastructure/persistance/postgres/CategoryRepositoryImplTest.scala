package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{IO, Resource}
import doobie.ConnectionIO
import org.scalatest.ParallelTestExecution
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._
import ua.pomo.catalog.shared._

import java.util.UUID

class CategoryRepositoryImplTest extends DbUnitTestSuite with ParallelTestExecution {
  import CategoryRepositoryImpl._
  override type Impl = CategoryRepository[ConnectionIO]
  override type TestResource = TestResources
  override val resourcePerTest: Boolean = true

  case class TestResources(db: DbResources, impls: Seq[(String, Impl)])
  override def getDbResources(resources: TestResources): DbResources = resources.db
  override def getImpls(resources: TestResource): Seq[(String, Impl)] = resources.impls
  override def names: Seq[String] = Seq("postgres", "inmemory")
  override def resource: Resource[IO, TestResource] =
    for {
      db <- Resources.dbTest
      pg <- Resource.pure(CategoryRepositoryImpl())
      inMem <- Resource.eval(CategoryRepositoryImpl.makeInMemory[ConnectionIO]).mapK[IO](db.xa.trans)
    } yield TestResources(db, Seq(("postgres", pg), ("inmemory", inMem)))

  test(s"queries") {
    val uuid = CategoryUUID(UUID.randomUUID())

    check(Queries.single(uuid))
    check(Queries.query(CategoryQuery(CategorySelector.All, PageToken.Two)))
    check(Queries.query(CategoryQuery(CategorySelector.All, PageToken.NonEmpty(10, 10))))
    check(Queries.query(CategoryQuery(CategorySelector.UidIs(uuid), PageToken.NonEmpty(10, 10))))
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
