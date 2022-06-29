package ua.pomo.catalog.infrastructure.persistance.postgres

import cats.effect.{Ref, Sync}
import cats.syntax.functor._
import cats.~>
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import ua.pomo.catalog.domain.PageToken
import ua.pomo.catalog.domain.category._

import java.util.UUID
import scala.util.chaining.scalaUtilChainingOps

class CategoryRepositoryImpl private () extends CategoryRepository[ConnectionIO] {
  import CategoryRepositoryImpl.Queries
  override def find(id: CategoryUUID): ConnectionIO[Option[Category]] = {
    Queries.single(id).option
  }

  override def get(id: CategoryUUID): doobie.ConnectionIO[Category] = {
    Queries.single(id).unique
  }

  override def query(req: CategoryQuery): ConnectionIO[List[Category]] = {
    Queries.query(req).to[List]
  }

  override def delete(id: CategoryUUID): ConnectionIO[Unit] = {
    Queries.delete(id).run.as(())
  }

  override def update(cat: UpdateCategory): ConnectionIO[Int] =
    Queries.update(cat).run

  override def create(category: CreateCategory): ConnectionIO[CategoryUUID] =
    Queries.insert(category).withUniqueGeneratedKeys[UUID]("id").map(CategoryUUID.apply)
}

object CategoryRepositoryImpl {
  def apply(): CategoryRepository[ConnectionIO] = new CategoryRepositoryImpl()
  def makeInMemory[F[_]: Sync]: F[CategoryRepository[F]] = {
    Ref[F].of(Map[CategoryUUID, Category]()).map(new InMemoryCategoryRepositoryImpl(_))
  }
  def withEffect[F[_]](transactor: ConnectionIO ~> F): CategoryRepository[F] = {
    new CategoryRepositoryImplF(new CategoryRepositoryImpl(), transactor)
  }

  private class CategoryRepositoryImplF[F[_]](underlying: CategoryRepository[ConnectionIO], xa: ConnectionIO ~> F)
      extends CategoryRepository[F] {
    override def create(category: CreateCategory): F[CategoryUUID] = underlying.create(category).pipe(xa.apply)

    override def get(id: CategoryUUID): F[Category] = underlying.get(id).pipe(xa.apply)

    override def find(id: CategoryUUID): F[Option[Category]] = underlying.find(id).pipe(xa.apply)

    override def query(req: CategoryQuery): F[List[Category]] = underlying.query(req).pipe(xa.apply)

    override def update(req: UpdateCategory): F[Int] = underlying.update(req).pipe(xa.apply)

    override def delete(id: CategoryUUID): F[Unit] = underlying.delete(id).pipe(xa.apply)
  }

  private[persistance] object Queries {
    def query(req: CategoryQuery): Query0[Category] = {
      val where = req.selector match {
        case CategorySelector.RidIs(rid) => fr"readable_id = $rid"
        case CategorySelector.UidIs(uid) => fr"id = $uid"
        case CategorySelector.All        => fr"1 = 1"
      }
      sql"""
        select cat.id, cat.readable_id, cat.display_name, cat.description
        from categories cat
        where $where
        order by cat.display_name
        ${compileToken(req.token)}
        """
        .query[Category]
    }

    def delete(id: CategoryUUID): Update0 = {
      sql"""delete from categories cat
            where id=$id""".update
    }

    def single(id: CategoryUUID): Query0[Category] =
      Queries.query(CategoryQuery(CategorySelector.UidIs(id), PageToken.Two))

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
