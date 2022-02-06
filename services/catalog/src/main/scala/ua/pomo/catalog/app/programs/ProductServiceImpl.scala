package ua.pomo.catalog.app.programs

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeErrorId, toFlatMapOps}
import cats.~>
import ua.pomo.catalog.domain.error.NotFound
import ua.pomo.catalog.domain.product._

class ProductServiceImpl[F[_]: Sync, G[_]: Sync] private (xa: G ~> F, repository: ProductRepository[G])
    extends ProductService[F] {
  override def create(command: CreateProduct): F[Product] = {
    repository.create(command).flatMap(repository.get).mapK(xa)
  }

  override def get(id: ProductId): F[Product] = {
    OptionT(repository.find(id))
      .getOrElseF(NotFound("product", id).raiseError[G, Product])
      .mapK(xa)
  }

  override def query(query: ProductQuery): F[List[Product]] = {
    repository.query(query).mapK(xa)
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
    repository.delete(id).mapK(xa)
  }
}
