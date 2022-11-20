//package ua.pomo.catalog.infrastructure.persistance.postgres
//
//import cats.effect.{IO, Resource}
//import doobie.ConnectionIO
//import doobie.implicits._
//import org.scalatest.ParallelTestExecution
//import ua.pomo.catalog.shared.Generators
//import ua.pomo.catalog.domain.category.{CategoryRepository, CategoryUUID}
//import ua.pomo.catalog.domain.imageList._
//import ua.pomo.catalog.domain.model._
//import ua.pomo.catalog.shared._
//import ua.pomo.common.{DbResources, DbUnitTestSuite}
//import ua.pomo.common.domain.repository.{PageToken, Query}
//import ua.pomo.common.infrastracture.persistance.RepositoryK
//import ua.pomo.common.infrastracture.persistance.postgres.Queries
//import ua.pomo.common.UnsafeRunnable._
//
//import java.util.UUID
//
//class ModelRepositoryImplTest extends DbUnitTestSuite with ForEachImpl with ParallelTestExecution with Fixtures {
//  case class TestResources(
//      categoryRepo: CategoryRepository[IO],
//      imageListRepo: ImageListRepository[IO],
//      postgres: ModelRepository[IO],
//      db: DbResources,
//      impls: Seq[(String, Impl)]
//  )
//
//  override type TestResource = TestResources
//  override val resourcePerTest: Boolean = true
//  override type Impl = ModelRepository[IO]
//  override def getDbResources(resources: TestResources): DbResources = resources.db
//  override def getImpls(resources: TestResource): scala.Seq[(String, Impl)] = resources.impls
//  override def names: Seq[String] = Seq("postgres", "inmemory")
//  override protected def resource: Resource[IO, TestResource] =
//    for {
//      db <- Resources.dbTest
//      modelInMemory <- Resource.eval(ModelRepositoryImpl.makeInMemory[IO])
//      modelRepo = RepositoryK(ModelRepositoryImpl(), db.xa.trans)
//    } yield TestResources(
//      RepositoryK(CategoryRepositoryImpl(), db.xa.trans),
//      RepositoryK(ImageListRepositoryImpl(), db.xa.trans),
//      RepositoryK(modelRepo),
//      db,
//      Seq(("postgres", modelRepo), ("inmemory", modelInMemory))
//    )
//
//  test("queries") {
//    val modelId = ModelId(UUID.randomUUID())
//    val categoryId = CategoryUUID(UUID.randomUUID())
//    val imageListId = ImageListId(UUID.randomUUID())
//    check(ModelQueries.find(Query(ModelSelector.CategoryIdIs(categoryId), PageToken.NonEmpty(10, 0))))
//    check(ModelQueries.delete(modelId))
//    check(ModelQueries.create(Generators.Model.createGen(imageListId, List.empty).sample.get)._1)
//    check(ModelQueries.update(Generators.Model.updateGen(imageListId, categoryId).sample.get))
//  }
//
//  testEachImplR(s"create get delete flow") { (res, impl) =>
//    abstract class fa(
//        val categoryRepo: CategoryRepository[IO],
//        val imageListRepo: ImageListRepository[IO]
//    ) extends ImageListFixture
//        with CategoryFixture
//    object f extends fa(res.categoryRepo, res.imageListRepo)
//
//    forAll(Generators.Model.createGen(f.imageListId1, List.empty)) { createModel1 =>
//      val createModel = createModel1.copy(categoryId = f.categoryId1)
//
//      val id = impl.create(createModel).trRun()
//      val found = impl.get(id).trRun()
//
//      createModel.categoryId should equal(found.categoryUid)
//      createModel.description should equal(found.description)
//      createModel.displayName should equal(found.displayName)
//      createModel.readableId should equal(found.readableId)
//      createModel.imageListId should equal(found.imageList.id)
//
//      impl.delete(id).trRun()
//    }
//  }
//
//  testEachImplR(s"update") { (res, impl) =>
//    abstract class fa(
//        val imageListRepo: ImageListRepository[IO],
//        val categoryRepo: CategoryRepository[IO],
//        val modelRepo: ModelRepository[IO]
//    ) extends ModelFixture
//    object f extends fa(res.imageListRepo, res.categoryRepo, impl)
//
//    forAll(Generators.Model.updateGen(f.imageListId2, f.categoryId2)) { update =>
//      impl.update(update.copy(id = f.modelId)).trRun()
//      val model = impl.get(f.modelId).trRun()
//
//      update.categoryId.foreach(_ should equal(model.categoryUid))
//      update.description.foreach(_ should equal(model.description))
//      update.displayName.foreach(_ should equal(model.displayName))
//      update.readableId.foreach(_ should equal(model.readableId))
//      update.imageListId.foreach(_ should equal(model.imageList.id))
//    }
//  }
//}
