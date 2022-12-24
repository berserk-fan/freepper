package ua.pomo.catalog.app

import cats.{MonadThrow, ~>}
import org.typelevel.log4cats.LoggerFactory
import ua.pomo.catalog.domain.Registry
import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.{ImageCrud, ImageDataRepository}
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.app.programs.{BasicService, ServiceK}
import ua.pomo.common.domain.crud.{Crud, Repository, Service}

package object programs {
  def serviceRegistry[F[_]: MonadThrow, G[_]: MonadThrow: LoggerFactory](
      r: Registry[Lambda[`T <: Crud` => Repository[F, T]]],
      xa: F ~> G,
      imageDataRepository: ImageDataRepository[G]
  ): Registry[Lambda[`T <: Crud` => Service[G, T]]] = {
    new Registry[Lambda[`T <: Crud` => Service[G, T]]] {
      override def category: Service[G, CategoryCrud] = ServiceK(BasicService(r.category), xa)
      override def image: Service[G, ImageCrud] = ImageServiceImpl(r.image, imageDataRepository, xa)
      override def imageList: Service[G, ImageListCrud] = ServiceK(BasicService(r.imageList), xa)
      override def model: Service[G, ModelCrud] = ServiceK(BasicService(r.model), xa)
      override def product: Service[G, ProductCrud] = ServiceK(BasicService(r.product), xa)
      override def parameterList: Service[G, ParameterListCrud] = ServiceK(BasicService(r.parameterList), xa)
    }
  }
}
