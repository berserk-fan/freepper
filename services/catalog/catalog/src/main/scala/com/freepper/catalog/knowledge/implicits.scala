package com.freepper.catalog.knowledge

import com.freepper.catalog.domain.category.{CategoryCrud, CategoryId, CreateCategory, UpdateCategory}
import com.freepper.catalog.domain.image.{BuzzImageUpdate, CreateImage, ImageCrud, ImageId}
import com.freepper.catalog.domain.imageList.{CreateImageList, ImageListCrud, ImageListId, UpdateImageList}
import com.freepper.catalog.domain.model.{CreateModel, ModelCrud, ModelId, UpdateModel}
import com.freepper.catalog.domain.parameter.{
  CreateParameterList,
  ParameterListCrud,
  ParameterListId,
  UpdateParameterList
}
import com.freepper.catalog.domain.product.{CreateProduct, ProductCrud, ProductId, UpdateProduct}
import com.freepper.common.domain.TypeName
import com.freepper.common.domain.crud
import com.freepper.common.domain.crud.{PageToken, Query}
import monocle.Getter
import monocle.macros.GenLens

object implicits {
  // update id getters
  given Getter[UpdateCategory, CategoryId] = GenLens[UpdateCategory](_.id)

  given Getter[UpdateImageList, ImageListId] = GenLens[UpdateImageList](_.id)

  given Getter[BuzzImageUpdate, ImageId] = GenLens[BuzzImageUpdate](_.id)

  given Getter[UpdateProduct, ProductId] = GenLens[UpdateProduct](_.id)

  given Getter[UpdateModel, ModelId] = GenLens[UpdateModel](_.id)

  given Getter[UpdateParameterList, ParameterListId] = GenLens[UpdateParameterList](_.id)

  // create id getters
  given Getter[CreateCategory, CategoryId] = GenLens[CreateCategory](_.id)

  given Getter[CreateImageList, ImageListId] = GenLens[CreateImageList](_.id)

  given Getter[CreateImage, ImageId] = GenLens[CreateImage](_.id)

  given Getter[CreateProduct, ProductId] = GenLens[CreateProduct](_.id)

  given Getter[CreateModel, ModelId] = GenLens[CreateModel](_.id)

  given Getter[CreateParameterList, ParameterListId] = GenLens[CreateParameterList](_.id)

  // query page getters
  given fromQuery[T]: Getter[crud.Query[T], PageToken.NonEmpty] = (s: Query[T]) => s.page

  // type names
  given TypeName[CategoryCrud] with
    override def name: String = "category"

  given TypeName[ImageCrud] with
    override def name: String = "image"

  given TypeName[ImageListCrud] with
    override def name: String = "imageList"

  given TypeName[ModelCrud] with
    override def name: String = "model"

  given TypeName[ProductCrud] with
    override def name: String = "product"

  given TypeName[ParameterListCrud] with
    override def name: String = "parameterList"

}
