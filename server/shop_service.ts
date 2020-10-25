/* eslint-disable */
import { GetCategoryRequest, Category, GetProductRequest, Product, ListProductsRequest, ListProductsResponse } from './shop_model';


export interface ShopService {

  getCategory(request: GetCategoryRequest): Promise<Category>;

  getProduct(request: GetProductRequest): Promise<Product>;

  listProducts(request: ListProductsRequest): Promise<ListProductsResponse>;

}

export const protobufPackage = 'pogladitMozhno.shop.v1'
