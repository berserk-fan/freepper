package ua.pomo.catalog.infrastructure.persistance

import cats.effect.{Ref, Sync}
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import shapeless._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._

import scala.collection.mutable

class CategoryRepositoryImpl private () extends CategoryRepository[ConnectionIO] {
  import CategoryRepositoryImpl.Queries
  override def find(id: CategoryId): ConnectionIO[Option[Category]] = {
    Queries.single(id).option
  }

  override def get(id: CategoryId): doobie.ConnectionIO[Category] = {
    Queries.single(id).unique
  }

  override def query(req: CategoryQuery): ConnectionIO[List[Category]] = {
    Queries.query(req).to[List]
  }

  override def delete(id: CategoryId): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
  }

  override def update(cat: UpdateCategory): ConnectionIO[Int] =
    Queries.update(cat).run

  override def create(category: CreateCategory): ConnectionIO[CategoryId] =
    Queries.insert(category).withUniqueGeneratedKeys[CategoryId]("id")
}

object CategoryRepositoryImpl {
  def apply(): CategoryRepository[ConnectionIO] = new CategoryRepositoryImpl()
  def makeInMemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(Map[CategoryId, Category]()).map(new InMemoryCategoryRepositoryImpl(_))
  }

  private[persistance] object Queries {
    def query(req: CategoryQuery): Query0[Category] = {
      val where = req.selector match {
        case CategorySelector.IdIs(categoryId) => fr"id = $categoryId"
        case CategorySelector.All              => fr"1 = 1"
      }
      sql"""
        select cat.id, cat.readable_id, cat.display_name, cat.description
        from categories cat
        where $where
        order by cat.display_name
        limit ${req.token.size}
        offset ${req.token.offset}
        """
        .query[
          CategoryId :: CategoryReadableId :: CategoryDisplayName :: CategoryDescription :: HNil
        ]
        .map { Generic[Category].from(_) }
    }

    def delete(id: CategoryId): Update0 = {
      sql"""delete from categories cat
            where id=$id""".update
    }

    def single(id: CategoryId) = Queries.query(CategoryQuery(CategorySelector.IdIs(id), PageToken.Two))

    def update(cat: UpdateCategory): Update0 = {
      val rId = cat.readableId.map(x => fr"readable_id = $x")
      val dName = cat.displayName.map(x => fr"display_name = $x")
      val desc = cat.description.map(x => fr"description = $x")

      val setFr = Fragments.setOpt(List(rId, dName, desc): _*)
      if (Fragment.empty == setFr) {
        throw new IllegalArgumentException("Empty update in updateCategory")
      }
      sql"""
           update categories cat
           $setFr
           where id=${cat.id}
         """.update
    }

    def insert(cat: CreateCategory): Update0 = {
      sql"""
           insert into categories (readable_id, display_name, description)
           VALUES (${cat.readableId}, ${cat.displayName}, ${cat.description})
         """.update
    }
  }
}
