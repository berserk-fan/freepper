package com.freepper.catalog.domain

import com.freepper.catalog.domain.category.CategoryCrud
import com.freepper.catalog.domain.image.ImageCrud
import com.freepper.catalog.domain.imageList.ImageListCrud
import com.freepper.catalog.domain.model.ModelCrud
import com.freepper.catalog.domain.parameter.ParameterListCrud
import com.freepper.catalog.domain.product.ProductCrud

trait Registry[F[_[_]]] {
  def category: F[CategoryCrud]
  def image: F[ImageCrud]
  def imageList: F[ImageListCrud]
  def model: F[ModelCrud]
  def product: F[ProductCrud]
  def parameterList: F[ParameterListCrud]
}

object Registry {
  def apply[F[_[_]]](
      c: F[CategoryCrud],
      i: F[ImageCrud],
      il: F[ImageListCrud],
      m: F[ModelCrud],
      p: F[ProductCrud],
      pl: F[ParameterListCrud]
  ): Registry[F] =
    new Registry[F] {
      override def category: F[CategoryCrud] = c
      override def product: F[ProductCrud] = p
      override def model: F[ModelCrud] = m
      override def image: F[ImageCrud] = i
      override def imageList: F[ImageListCrud] = il
      override def parameterList: F[ParameterListCrud] = pl
    }
}
