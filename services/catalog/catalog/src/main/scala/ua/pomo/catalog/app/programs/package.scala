package ua.pomo.catalog.app

import cats.{MonadThrow, ~>}
import ua.pomo.catalog.domain.Registry
import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.{ImageCrud, ImageDataRepository}
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.app.programs.{BasicService, ServiceK}
import ua.pomo.common.domain.{crud, registry}
import ua.pomo.common.domain.crud.{Crud, PageToken, Repository, Service, ServiceOps}
import ua.pomo.common.domain.registry.RegistryMapper2

package object programs {
  def serviceRegistry[F[_]: MonadThrow, G[_]](
      r: Registry[Lambda[`T <: Crud` => Repository[F, T]]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[Lambda[`T <: Crud` => Service[G, T]]] = {
    val so = Registry.usingImplicits[ServiceOps]

    Registry.fromUntyped {
      registry.Registry.map2(so.toUntyped, r.toUntyped) {
        new RegistryMapper2[ServiceOps, Lambda[`T <: Crud` => Repository[F, T]], Lambda[`T <: Crud` => Service[G, T]]] {
          override def apply[T <: crud.Crud](so: ServiceOps[T], f: Repository[F, T]): Service[G, T] = {
            ServiceK(BasicService(f)(implicitly, so), xa)
          }
        }
      }
    }
  }
}
