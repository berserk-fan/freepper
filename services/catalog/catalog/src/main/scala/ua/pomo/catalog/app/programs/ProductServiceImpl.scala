package ua.pomo.catalog.app.programs

import cats.arrow.FunctionK
import cats.data.OptionT
import cats.effect.Sync
import cats.effect.kernel.Async
import cats.implicits.{catsSyntaxApplicativeErrorId, catsSyntaxApplicativeId, toFlatMapOps, toFunctorOps}
import cats.~>
import doobie.{ConnectionIO, Transactor}
import ua.pomo.catalog.domain.product._
import ua.pomo.catalog.infrastructure.persistance.postgres.ProductRepository
import ua.pomo.common.domain.error.NotFound

private class ProductServiceImpl[F[_], G[_]: Sync] private (xa: G ~> F, repository: ProductRepository[G])
    extends ProductService[F] {
  override def create(command: CreateProduct): F[Product] = {
    repository.create(command).flatMap(repository.get).mapK(xa)
  }

  override def get(id: ProductId): F[Product] = {
    OptionT(repository.find(id))
      .getOrElseF(NotFound("product", id).raiseError[G, Product])
      .mapK(xa)
  }

  override def query(query: ProductQuery): F[FindProductResponse] = {
    repository
      .findAll(query)
      .map(res => FindProductResponse(res, computeNextPageToken(query.page, res)))
      .mapK(xa)
  }

  override def update(command: UpdateProduct): F[Product] = {
    repository
      .update(command)
      .flatMap { updated =>
        if (updated == 0) {
          NotFound("product", command.id).raiseError[G, Product]
        } else {
          repository.get(command.id)
        }
      }
      .mapK(xa)
  }

  override def delete(id: ProductId): F[Unit] = {
    repository
      .delete(id)
      .flatMap { deleted =>
        if (deleted == 0) {
          NotFound("product", id).raiseError[G, Unit]
        } else {
          ().pure[G]
        }
      }
      .mapK(xa)
  }
}

object ProductServiceImpl {
  def makeInMemory[F[_]: Sync]: F[ProductService[F]] = {
    ProductRepository
      .inmemory[F]
      .map(new ProductServiceImpl[F, F](FunctionK.id[F], _))
  }

  def apply[F[_]: Async](transactor: Transactor[F], repository: ProductRepository[ConnectionIO]): ProductService[F] = {
    new ProductServiceImpl[F, ConnectionIO](transactor.trans, repository)
  }
}
