package ua.pomo.catalog.domain

import ua.pomo.catalog.domain.category.CategoryCrud
import ua.pomo.catalog.domain.image.ImageCrud
import ua.pomo.catalog.domain.imageList.ImageListCrud
import ua.pomo.catalog.domain.model.ModelCrud
import ua.pomo.catalog.domain.parameter.ParameterListCrud
import ua.pomo.catalog.domain.product.ProductCrud
import ua.pomo.common.domain.crud.Crud
import ua.pomo.common.domain.registry

trait Registry[F[_ <: Crud]] {
  def category: F[CategoryCrud]

  def image: F[ImageCrud]

  def imageList: F[ImageListCrud]

  def model: F[ModelCrud]

  def product: F[ProductCrud]

  def parameterList: F[ParameterListCrud]
}

object Registry {
  def fromUntyped[F[_ <: Crud]](r: registry.Registry[F]): Registry[F] = new Registry[F] {
    override def category: F[CategoryCrud] = r.apply[CategoryCrud]

    override def image: F[ImageCrud] = r.apply[ImageCrud]

    override def imageList: F[ImageListCrud] = r.apply[ImageListCrud]

    override def model: F[ModelCrud] = r.apply[ModelCrud]

    override def product: F[ProductCrud] = r.apply[ProductCrud]

    override def parameterList: F[ParameterListCrud] = r.apply[ParameterListCrud]
  }

  def usingImplicits[F[_ <: Crud]](implicit
      f1: F[CategoryCrud],
      f2: F[ImageCrud],
      f3: F[ImageListCrud],
      f4: F[ModelCrud],
      f5: F[ProductCrud],
      f6: F[ParameterListCrud]
  ): Registry[F] = {
    new Registry[F] {
      override def category: F[CategoryCrud] = f1

      override def image: F[ImageCrud] = f2

      override def imageList: F[ImageListCrud] = f3

      override def model: F[ModelCrud] = f4

      override def product: F[ProductCrud] = f5

      override def parameterList: F[ParameterListCrud] = f6
    }
  }

  implicit class RegistryOps[F[_ <: Crud]](r: Registry[F]) {
    def toUntyped: registry.Registry[F] = new registry.Registry[F] {
      override def apply[T <: Crud: ValueOf]: F[T] = {
        val res = implicitly[ValueOf[T]].value match {
          case category.Crud  => r.category
          case image.Crud     => r.image
          case imageList.Crud => r.imageList
          case model.Crud     => r.model
          case product.Crud   => r.product
          case parameter.Crud => r.parameterList
          case _              => throw new IllegalArgumentException("Unknown crud. Pls add this case to Registry type")
        }
        res.asInstanceOf[F[T]]
      }
    }
  }
}
