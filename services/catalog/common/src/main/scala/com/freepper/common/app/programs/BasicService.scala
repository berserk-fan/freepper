package com.freepper.common.app.programs

import cats.{MonadThrow, Show}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import com.freepper.common.domain.crud.{Crud, ListResponse, PageToken, Query, Repository, Service}
import com.freepper.common.domain.error.NotFound
import monocle.Getter

case class BasicService[G[_]: MonadThrow, T <: Crud: ValueOf](repository: Repository[G, T])(implicit
    updateToId: Getter[T#Update, T#EntityId],
    crudShow: Show[T]
) extends Service[G, T] {

  private val entityDisplayName: String = crudShow.show(implicitly[ValueOf[T]].value)

  def create(command: T#Create): G[T#Entity] = repository.create(command).flatMap(repository.get)

  def delete(id: T#EntityId): G[Unit] =
    repository
      .delete(id)
      .flatMap { deleted =>
        if (deleted == 0) {
          MonadThrow[G].raiseError(NotFound(entityDisplayName, id))
        } else {
          MonadThrow[G].unit
        }
      }

  def findAll(req: Query[T#Selector]): G[ListResponse[T#Entity]] =
    repository
      .findAll(req)
      .map(entities => ListResponse(entities, computeNextPageToken(req.page, entities)))

  def get(id: T#EntityId): G[T#Entity] =
    repository
      .find(id)
      .flatMap {
        case Some(value) => MonadThrow[G].pure(value)
        case None =>
          MonadThrow[G].raiseError[T#Entity](NotFound(entityDisplayName, id))
      }

  def update(command: T#Update): G[T#Entity] =
    repository
      .update(command)
      .flatMap { updated =>
        if (updated == 0) {
          MonadThrow[G].raiseError[T#Entity](
            NotFound(entityDisplayName, updateToId.get(command))
          )
        } else {
          repository.get(updateToId.get(command))
        }
      }

  private def computeNextPageToken[U](page: PageToken.NonEmpty, res: List[U]): PageToken = {
    val nextPage = if (res.length != page.size) {
      PageToken.Empty
    } else {
      PageToken.NonEmpty(page.size, page.size + page.offset)
    }
    nextPage
  }
}
