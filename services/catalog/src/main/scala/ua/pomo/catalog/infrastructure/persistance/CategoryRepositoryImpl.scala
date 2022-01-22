package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{Ref, Sync}
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import shapeless._
import ua.pomo.catalog.domain.category._

import scala.collection.mutable

class CategoryRepositoryImpl private () extends CategoryRepository[ConnectionIO] {
  import CategoryRepositoryImpl.Queries
  override def find(id: CategoryId): ConnectionIO[Option[Category]] = {
    Queries.findCategory(id).option
  }

  override def get(id: CategoryId): doobie.ConnectionIO[Category] = {
    Queries.findCategory(id).unique
  }

  override def findAll(): ConnectionIO[List[Category]] = {
    Queries.findCategories.to[List]
  }

  override def delete(id: CategoryId): ConnectionIO[Unit] = {
    Queries.deleteCategory(id).run.as(())
  }

  override def update(cat: UpdateCategory): ConnectionIO[Int] =
    Queries
      .updateCategory(cat)
      .run

  override def create(category: Category): ConnectionIO[CategoryUUID] =
    Queries
      .insertCategory(category)
      .withUniqueGeneratedKeys[CategoryUUID]("id")
}

object CategoryRepositoryImpl {
  def apply(): CategoryRepository[ConnectionIO] = new CategoryRepositoryImpl()
  def makeInMemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(mutable.Map[CategoryId, Category]()).map(new InMemoryCategoryRepositoryImpl(_))
  }

  private[persistance] object Queries {
    private def toWhereClause(id: Option[CategoryId]): Fragment = {
      id.map(_.value.fold(uuid => fr"cat.id = $uuid", readableId => fr"cat.readable_id = $readableId"))
        .map(fr"where " ++ _)
        .getOrElse(Fragment.empty)
    }

    private def selectCategories(id: Option[CategoryId]): Query0[Category] = {
      sql"""
        select cat.id, cat.readable_id, cat.display_name, cat.description
        from categories cat
        ${toWhereClause(id)}"""
        .query[
          CategoryUUID :: CategoryReadableId :: CategoryDisplayName :: CategoryDescription :: HNil
        ]
        .map { Generic[Category].from(_) }
    }

    def findCategory(id: CategoryId): Query0[Category] = selectCategories(Some(id))

    val findCategories: Query0[Category] = selectCategories(None)

    def deleteCategory(id: CategoryId): Update0 = {
      sql"""delete from categories cat
            ${toWhereClause(Some(id))}""".update
    }

    def updateCategory(cat: UpdateCategory): Update0 = {
      val rId   = cat.readableId.map(x => fr"readable_id = $x")
      val dName = cat.displayName.map(x => fr"display_name = $x")
      val desc  = cat.description.map(x => fr"description = $x")

      val setFr = Fragments.setOpt(List(rId, dName, desc): _*)
      if (Fragment.empty == setFr) {
        throw new IllegalArgumentException("Empty update in updateCategory")
      }
      sql"""
           update categories cat
           $setFr
           ${toWhereClause(Some(cat.id))}
         """.update
    }

    def insertCategory(cat: Category): Update0 = {
      sql"""
           insert into categories (readable_id, display_name, description)
           VALUES (${cat.readableId}, ${cat.displayName}, ${cat.description})
         """.update
    }
  }
}
