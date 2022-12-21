package ua.pomo.common.app.programs

import cats.MonadThrow
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import ua.pomo.common.domain.crud.{Crud, ListResponse, PageToken, Query, Repository, Service, ServiceOps}
import ua.pomo.common.domain.error.NotFound

class AbstractService[G[_]: MonadThrow, T <: Crud: ServiceOps] private (repository: Repository[G, T])
    extends Service[G, T] {

  def create(command: T#Create): G[T#Entity] = repository.create(command).flatMap(repository.get)

  def delete(id: T#EntityId): G[Unit] =
    repository
      .delete(id)
      .flatMap { deleted =>
        if (deleted == 0) {
          MonadThrow[G].raiseError(NotFound(ServiceOps[T].entityDisplayName.value, id))
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
        case None        => MonadThrow[G].raiseError[T#Entity](NotFound(ServiceOps[T].entityDisplayName.value, id))
      }

  def update(command: T#Update): G[T#Entity] =
    repository
      .update(command)
      .flatMap { updated =>
        if (updated == 0) {
          MonadThrow[G].raiseError[T#Entity](
            NotFound(ServiceOps[T].entityDisplayName.value, ServiceOps[T].getIdUpdate(command))
          )
        } else {
          repository.get(ServiceOps[T].getIdUpdate(command))
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
