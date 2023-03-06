package com.freepper.catalog.app

import cats.data.Kleisli
import cats.{MonadThrow, ~>}
import com.freepper.catalog.domain.RegistryHelper
import org.typelevel.log4cats.LoggerFactory
import com.freepper.catalog.domain.category.CategoryCrud
import com.freepper.catalog.domain.image.{ImageCrud, ImageDataRepository}
import com.freepper.catalog.domain.imageList.ImageListCrud
import com.freepper.catalog.domain.model.ModelCrud
import com.freepper.catalog.domain.parameter.ParameterListCrud
import com.freepper.catalog.domain.product.ProductCrud
import com.freepper.common.app.programs.{BasicService, SecuredService, ServiceK}
import com.freepper.common.domain.auth.CallContext
import com.freepper.common.domain.crud.{Crud, Repository, Service}
import com.freepper.common.domain.registry._

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
