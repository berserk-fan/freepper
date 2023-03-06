package com.freepper.catalog.domain

import com.freepper.catalog.domain.category.CategoryCrud
import com.freepper.catalog.domain.image.ImageCrud
import com.freepper.catalog.domain.imageList.ImageListCrud
import com.freepper.catalog.domain.model.ModelCrud
import com.freepper.catalog.domain.parameter.ParameterListCrud
import com.freepper.catalog.domain.product.ProductCrud
import com.freepper.common.domain.crud.Crud
import com.freepper.common.domain.registry.Registry

object RegistryHelper {
  object implicits {
    implicit class RegistryOps[F[_ <: Crud]](r: Registry[F]) {
      def category: F[CategoryCrud] = r.apply[CategoryCrud]
      def image: F[ImageCrud] = r.apply[ImageCrud]
      def imageList: F[ImageListCrud] = r.apply[ImageListCrud]
      def model: F[ModelCrud] = r.apply[ModelCrud]
      def product: F[ProductCrud] = r.apply[ProductCrud]
      def parameterList: F[ParameterListCrud] = r.apply[ParameterListCrud]
    }
  }

  def createRegistry[F[_ <: Crud]](
      f1: F[CategoryCrud],
      f2: F[ImageCrud],
      f3: F[ImageListCrud],
      f4: F[ModelCrud],
      f5: F[ProductCrud],
      f6: F[ParameterListCrud]
  ): Registry[F] = {
    new Registry[F] {
      override def apply[T <: Crud: ValueOf]: F[T] = implicitly[ValueOf[T]].value match {
        case category.Crud  => f1.asInstanceOf[F[T]]
        case image.Crud     => f2.asInstanceOf[F[T]]
        case imageList.Crud => f3.asInstanceOf[F[T]]
        case model.Crud     => f4.asInstanceOf[F[T]]
        case product.Crud   => f5.asInstanceOf[F[T]]
        case parameter.Crud => f6.asInstanceOf[F[T]]
        case _ => throw new IllegalArgumentException()
      }
    }
  }

  def usingImplicits[F[_ <: Crud]](implicit
      f1: F[CategoryCrud],
      f2: F[ImageCrud],
      f3: F[ImageListCrud],
      f4: F[ModelCrud],
      f5: F[ProductCrud],
      f6: F[ParameterListCrud]
  ): Registry[F] = { createRegistry(f1, f2, f3, f4, f5, f6) }
}
