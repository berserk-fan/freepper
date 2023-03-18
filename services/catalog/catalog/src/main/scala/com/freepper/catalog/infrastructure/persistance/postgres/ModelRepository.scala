package com.freepper.catalog.infrastructure.persistance.postgres

import cats.data.NonEmptyList
import cats.effect.{Ref, Sync}
import cats.implicits.toFunctorOps
import doobie._
import doobie.implicits.toSqlInterpolator
import doobie.postgres.implicits.UuidType
import monocle.syntax.all._
import shapeless.Generic
import squants.market.{Money, USD}
import com.freepper.catalog.domain.category.{CategoryId, CategoryReadableId}
import com.freepper.catalog.domain.imageList._
import com.freepper.catalog.domain.model
import com.freepper.catalog.domain.model._
import com.freepper.catalog.domain.parameter.{ParamListDisplayName, ParameterList}
import com.freepper.common.domain.error.DbErr
import com.freepper.common.infrastracture.persistance.inmemory.{AbstractInMemoryRepository, InMemoryUpdaterPoly}
import com.freepper.common.infrastracture.persistance.postgres.{
  AbstractPostgresRepository,
  DbUpdaterPoly,
  Queries,
  QueryHelpers
}

import java.util.UUID

object ModelRepository {
  def postgres: ModelRepository[ConnectionIO] = {
    new ModelRepositoryImpl()
  }

  def inmemory[F[_]: Sync]: F[ModelRepository[F]] = {
    Ref[F]
      .of(Map[ModelId, Model]())
      .map(
        new ModelInMemoryRepositoryImpl[F](_)
      )
  }

  private class ModelRepositoryImpl() extends AbstractPostgresRepository[model.Crud.type](ModelQueries) {
    override def idSelector: ModelId => ModelSelector = (id: ModelId) => ModelSelector.IdIs(id)
  }

  object ModelQueries extends Queries[ModelCrud] {
    Read[Double].map(x => ModelMinimalPrice(Money(x, USD)))

    override def create(req: CreateModel): List[doobie.Update0] = List({
      val modelId = req.id.value
      val modelsInsert =
        sql"""
            INSERT INTO models (id, readable_id, display_name, description, category_id, image_list_id)
             VALUES ($modelId,
                     ${req.readableId},
                     ${req.displayName},
                     ${req.description},
                     ${req.categoryId},
                     ${req.imageListId}
                   )
             """

      val res = NonEmptyList.fromList(req.parameterListIds).fold(modelsInsert) { paramsNonEmpty =>
        val mplValues = Fragments.values(paramsNonEmpty.map((modelId, _)))
        sql"""
             WITH modelsInsert as (
               $modelsInsert
             )
             INSERT INTO model_parameter_lists (model_id, parameter_list_id) $mplValues
            """
      }

      res.update
    })

    override def delete(id: ModelId): List[doobie.Update0] = List {
      sql"""
             delete from models m
             where id=$id
           """.update
    }

    private def compileWhere(where: ModelSelector): Fragment = {
      where match {
        case ModelSelector.All              => fr"1 = 1"
        case ModelSelector.IdIs(id)         => fr"m.id = $id"
        case ModelSelector.CategoryIdIs(id) => fr"m.category_id = $id"
        case ModelSelector.RidIs(id)        => fr"readable_id = $id"
      }
    }

    override def find(query: ModelQuery): Query0[Model] = {
      implicit val readImages: Get[Model] = readJsonFromView[Model]

      sql"""
          select m.json
          from models_prebuilt m
          where ${compileWhere(query.selector)}
          order by m.create_time desc
          ${compileToken(query.page)}
        """
        .query[Model]
    }

    object updaterObj extends DbUpdaterPoly {
      implicit val a1: Res[ModelReadableId] = gen("readable_id")
      implicit val a2: Res[ModelDescription] = gen("description")
      implicit val a3: Res[CategoryId] = gen("category_id")
      implicit val a4: Res[ModelDisplayName] = gen("display_name")
      implicit val a5: Res[ImageListId] = gen("image_list_id")
    }

    override def update(req: UpdateModel): List[doobie.Update0] = {
      QueryHelpers.defaultUpdateRaw(Generic[UpdateModel].to(req), req.id, updaterObj, "models").map(_.update).toList
    }
  }

  case class ModelInMemoryRepositoryImpl[F[_]: Sync] private[persistance] (ref: Ref[F, Map[ModelId, Model]])
      extends AbstractInMemoryRepository[F, model.Crud.type](ref) {
    override def creator: CreateModel => Model = (req: CreateModel) =>
      Model(
        ModelId(UUID.randomUUID()),
        req.readableId,
        req.categoryId,
        CategoryReadableId("some-category"),
        req.displayName,
        req.description,
        ModelMinimalPrice(Money(0, USD)),
        req.parameterListIds.map(id => ParameterList(id, ParamListDisplayName(""), List())),
        ImageList(req.imageListId, ImageListDisplayName(""), List())
      )

    override def filter: ModelSelector => Model => Boolean = {
      case ModelSelector.All =>
        _ => true
      case ModelSelector.IdIs(id)         => _.id == id
      case ModelSelector.CategoryIdIs(id) => _.categoryUid == id
      case ModelSelector.RidIs(id)        => _.readableId == id
    }

    object updateObj extends InMemoryUpdaterPoly[Model] {
      implicit val readableId: Res[ModelReadableId] = gen(_.focus(_.readableId))
      implicit val categoryId: Res[CategoryId] = gen(_.focus(_.categoryUid))
      implicit val displayName: Res[ModelDisplayName] = gen(_.focus(_.displayName))
      implicit val description: Res[ModelDescription] = gen(_.focus(_.description))
      implicit val imageListId: Res[ImageListId] = gen(_.focus(_.imageList.id))
    }

    override def update(req: UpdateModel): F[Int] = {
      updateHelper(req, updateObj, Generic[UpdateModel])
    }
  }

}
