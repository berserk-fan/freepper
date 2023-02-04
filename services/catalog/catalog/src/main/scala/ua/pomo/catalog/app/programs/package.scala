package ua.pomo.catalog.app

import cats.data.Kleisli
import cats.{MonadThrow, ~>}
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.domain.RegistryHelper
import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.{ImageCrud, ImageDataRepository}
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.app.programs.{BasicService, SecuredService, ServiceK}
import ua.pomo.common.domain.auth.CallContext
import ua.pomo.common.domain.crud.{Crud, Repository, Service}
import ua.pomo.common.domain.registry._

package object programs {
  type ServiceMonad[F[_], T] = Kleisli[F, CallContext, T]

  def basicServiceRegistry[F[_]: MonadThrow, G[_]: MonadThrow: LoggerFactory](
      r: Registry[Lambda[`T <: Crud` => Repository[F, T]]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[Lambda[`T <: Crud` => Service[G, T]]] = {
    RegistryHelper.createRegistry(
      ServiceK(BasicService(r.apply[CategoryCrud]), xa),
      ImageServiceImpl(r.apply[ImageCrud], imageDataRepository, xa),
      ServiceK(BasicService(r.apply[ImageListCrud]), xa),
      ServiceK(BasicService(r.apply[ModelCrud]), xa),
      ServiceK(BasicService(r.apply[ProductCrud]), xa),
      ServiceK(BasicService(r.apply[ParameterListCrud]), xa)
    )
  }

  def serviceRegistry[F[_]: MonadThrow, G[_]: MonadThrow: LoggerFactory](
      r: Registry[Lambda[`T <: Crud` => Repository[F, T]]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[Lambda[`T <: Crud` => Service[ServiceMonad[G, *], T]]] = {
    val registry1 = basicServiceRegistry(r, xa, imageDataRepository)

    val addCommonConcerns = new RegistryMapper[
      Lambda[`T <: Crud` => Service[G, T]],
      Lambda[`T <: Crud` => Service[ServiceMonad[G, *], T]]
    ] {
      override def apply[T <: Crud](f: Service[G, T]): Service[ServiceMonad[G, *], T] = {
        SecuredService(f)
      }
    }

    Registry.map1(registry1)(addCommonConcerns)
  }
}
