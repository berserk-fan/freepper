package com.freepper.common.app.programs

import cats.MonadThrow
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import com.freepper.common.domain.crud.{Crud, ListResponse, PageToken, Repository, Service}
import com.freepper.common.domain.error.NotFound
import monocle.Getter
import Crud.*
import com.freepper.common.domain.TypeName

case class BasicService[G[_]: MonadThrow, C[_]](repository: Repository[G, C])(implicit
    updateToId: Getter[C[Update], C[EntityId]],
    queryToPage: Getter[C[Query], PageToken.NonEmpty],
    crudShow: TypeName[C]
) extends Service[G, C] {

  private val entityDisplayName: String = crudShow.name

  def create(command: C[Create]): G[C[Entity]] = repository.create(command).flatMap(repository.get)

  def delete(id: C[EntityId]): G[Unit] =
    repository
      .delete(id)
      .flatMap { deleted =>
        if (deleted == 0) {
          MonadThrow[G].raiseError(NotFound(entityDisplayName, id))
        } else {
          MonadThrow[G].unit
        }
      }

  def findAll(req: C[Query]): G[ListResponse[C[Entity]]] = {
    val page = queryToPage.get(req)
    repository
      .findAll(req)
      .map(entities => ListResponse(entities, computeNextPageToken(page, entities)))
  }

  def get(id: C[EntityId]): G[C[Entity]] =
    repository
      .find(id)
      .flatMap {
        case Some(value) => MonadThrow[G].pure(value)
        case None =>
          MonadThrow[G].raiseError[C[Entity]](NotFound(entityDisplayName, id))
      }

  def update(command: C[Update]): G[C[Entity]] =
    repository
      .update(command)
      .flatMap { updated =>
        if (updated == 0) {
          MonadThrow[G].raiseError[C[Entity]](
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
