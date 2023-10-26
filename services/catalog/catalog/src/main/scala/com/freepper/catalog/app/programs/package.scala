package com.freepper.catalog.app

import cats.data.Kleisli
import cats.{MonadThrow, ~>}
import org.typelevel.log4cats.LoggerFactory
import com.freepper.catalog.domain.category.CategoryCrud
import com.freepper.catalog.domain.image.{ImageCrud, ImageDataRepository}
import com.freepper.catalog.domain.imageList.ImageListCrud
import com.freepper.catalog.domain.model.ModelCrud
import com.freepper.catalog.domain.parameter.ParameterListCrud
import com.freepper.catalog.domain.product.ProductCrud
import com.freepper.common.app.programs.{BasicService, SecuredService, ServiceK}
import com.freepper.common.domain.auth.CallContext
import com.freepper.catalog.domain.Registry
import com.freepper.common.domain.crud.{Repository, Service}
import com.freepper.catalog.knowledge.implicits.given

package object programs {
  type ServiceMonad[F[_], T] = Kleisli[F, CallContext, T]

  def basicServiceRegistry[F[_]: MonadThrow, G[_]: MonadThrow: LoggerFactory](
      r: Registry[[C[_]] =>> Repository[F, C]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[[C[_]] =>> Service[G, C]] = {
    Registry(
      ServiceK(BasicService(r.category), xa),
      ImageServiceImpl(r.image, imageDataRepository, xa),
      ServiceK(BasicService(r.imageList), xa),
      ServiceK(BasicService(r.model), xa),
      ServiceK(BasicService(r.product), xa),
      ServiceK(BasicService(r.parameterList), xa)
    )
  }

  def serviceRegistry[F[_]: MonadThrow, G[_]: MonadThrow: LoggerFactory](
      r: Registry[[C[_]] =>> Repository[F, C]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[[C[_]] =>> Service[ServiceMonad[G, *], C]] = {
    val registry1 = basicServiceRegistry(r, xa, imageDataRepository)

    Registry.apply(
      SecuredService(registry1.category),
      SecuredService(registry1.image),
      SecuredService(registry1.imageList),
      SecuredService(registry1.model),
      SecuredService(registry1.product),
      SecuredService(registry1.parameterList)
    )
  }
}
