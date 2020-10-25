/* eslint-disable */


export interface GetCategoryRequest {
  name: string;
}

export interface GetProductRequest {
  name: string;
}

export interface ListProductsRequest {
  /**
   *  The parent resource name, for example, "categories/category1".
   */
  parent: string;
  /**
   *  The maximum number of items to return.
   */
  pageSize: number;
  /**
   *  The next_page_token value returned from a previous List request, if any.
   */
  pageToken: string;
}

export interface ListProductsResponse {
  /**
   * There will be a maximum number of items returned based on the page_size field
   *  in the request.
   */
  products: Product[];
  /**
   *  Token to retrieve the next page of results, or empty if there are no
   *  more results in the list.
   */
  nextPageToken: string;
}

export interface ImageData {
  src: string;
  alt: string;
}

export interface Category {
  name: string;
  displayName: string;
  id: number;
  description: string;
  image: ImageData | undefined;
  products: Product[];
}

export interface Color {
  id: number;
  title: string;
  displayName: string;
  description: string;
}

export interface Fabric {
  id: number;
  title: string;
  displayName: string;
  description: string;
  image: ImageData | undefined;
}

export interface Size {
  id: number;
  title: string;
  displayName: string;
  description: string;
}

export interface DogBed {
  fabric: Fabric | undefined;
  sizes: Size[];
}

export interface Product {
  name: string;
  displayName: string;
  id: number;
  description: string;
  details?: { $case: 'dogBed', dogBed: DogBed };
}

export const protobufPackage = 'pogladitMozhno.shop.v1'
