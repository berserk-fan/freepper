/* eslint-disable */
import { Writer, Reader } from 'protobufjs/minimal';


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

const baseGetCategoryRequest: object = {
  name: "",
};

const baseGetProductRequest: object = {
  name: "",
};

const baseListProductsRequest: object = {
  parent: "",
  pageSize: 0,
  pageToken: "",
};

const baseListProductsResponse: object = {
  nextPageToken: "",
};

const baseImageData: object = {
  src: "",
  alt: "",
};

const baseCategory: object = {
  name: "",
  displayName: "",
  id: 0,
  description: "",
};

const baseColor: object = {
  id: 0,
  title: "",
  displayName: "",
  description: "",
};

const baseFabric: object = {
  id: 0,
  title: "",
  displayName: "",
  description: "",
};

const baseSize: object = {
  id: 0,
  title: "",
  displayName: "",
  description: "",
};

const baseDogBed: object = {
};

const baseProduct: object = {
  name: "",
  displayName: "",
  id: 0,
  description: "",
};

export const protobufPackage = 'pogladitMozhno.shop.v1'

export const GetCategoryRequest = {
  encode(message: GetCategoryRequest, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.name);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): GetCategoryRequest {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseGetCategoryRequest } as GetCategoryRequest;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.name = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): GetCategoryRequest {
    const message = { ...baseGetCategoryRequest } as GetCategoryRequest;
    if (object.name !== undefined && object.name !== null) {
      message.name = String(object.name);
    }
    return message;
  },
  fromPartial(object: DeepPartial<GetCategoryRequest>): GetCategoryRequest {
    const message = { ...baseGetCategoryRequest } as GetCategoryRequest;
    if (object.name !== undefined && object.name !== null) {
      message.name = object.name;
    }
    return message;
  },
  toJSON(message: GetCategoryRequest): unknown {
    const obj: any = {};
    message.name !== undefined && (obj.name = message.name);
    return obj;
  },
};

export const GetProductRequest = {
  encode(message: GetProductRequest, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.name);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): GetProductRequest {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseGetProductRequest } as GetProductRequest;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.name = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): GetProductRequest {
    const message = { ...baseGetProductRequest } as GetProductRequest;
    if (object.name !== undefined && object.name !== null) {
      message.name = String(object.name);
    }
    return message;
  },
  fromPartial(object: DeepPartial<GetProductRequest>): GetProductRequest {
    const message = { ...baseGetProductRequest } as GetProductRequest;
    if (object.name !== undefined && object.name !== null) {
      message.name = object.name;
    }
    return message;
  },
  toJSON(message: GetProductRequest): unknown {
    const obj: any = {};
    message.name !== undefined && (obj.name = message.name);
    return obj;
  },
};

export const ListProductsRequest = {
  encode(message: ListProductsRequest, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.parent);
    writer.uint32(16).int32(message.pageSize);
    writer.uint32(26).string(message.pageToken);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): ListProductsRequest {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseListProductsRequest } as ListProductsRequest;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.parent = reader.string();
          break;
        case 2:
          message.pageSize = reader.int32();
          break;
        case 3:
          message.pageToken = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): ListProductsRequest {
    const message = { ...baseListProductsRequest } as ListProductsRequest;
    if (object.parent !== undefined && object.parent !== null) {
      message.parent = String(object.parent);
    }
    if (object.pageSize !== undefined && object.pageSize !== null) {
      message.pageSize = Number(object.pageSize);
    }
    if (object.pageToken !== undefined && object.pageToken !== null) {
      message.pageToken = String(object.pageToken);
    }
    return message;
  },
  fromPartial(object: DeepPartial<ListProductsRequest>): ListProductsRequest {
    const message = { ...baseListProductsRequest } as ListProductsRequest;
    if (object.parent !== undefined && object.parent !== null) {
      message.parent = object.parent;
    }
    if (object.pageSize !== undefined && object.pageSize !== null) {
      message.pageSize = object.pageSize;
    }
    if (object.pageToken !== undefined && object.pageToken !== null) {
      message.pageToken = object.pageToken;
    }
    return message;
  },
  toJSON(message: ListProductsRequest): unknown {
    const obj: any = {};
    message.parent !== undefined && (obj.parent = message.parent);
    message.pageSize !== undefined && (obj.pageSize = message.pageSize);
    message.pageToken !== undefined && (obj.pageToken = message.pageToken);
    return obj;
  },
};

export const ListProductsResponse = {
  encode(message: ListProductsResponse, writer: Writer = Writer.create()): Writer {
    for (const v of message.products) {
      Product.encode(v!, writer.uint32(10).fork()).ldelim();
    }
    writer.uint32(18).string(message.nextPageToken);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): ListProductsResponse {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseListProductsResponse } as ListProductsResponse;
    message.products = [];
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.products.push(Product.decode(reader, reader.uint32()));
          break;
        case 2:
          message.nextPageToken = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): ListProductsResponse {
    const message = { ...baseListProductsResponse } as ListProductsResponse;
    message.products = [];
    if (object.products !== undefined && object.products !== null) {
      for (const e of object.products) {
        message.products.push(Product.fromJSON(e));
      }
    }
    if (object.nextPageToken !== undefined && object.nextPageToken !== null) {
      message.nextPageToken = String(object.nextPageToken);
    }
    return message;
  },
  fromPartial(object: DeepPartial<ListProductsResponse>): ListProductsResponse {
    const message = { ...baseListProductsResponse } as ListProductsResponse;
    message.products = [];
    if (object.products !== undefined && object.products !== null) {
      for (const e of object.products) {
        message.products.push(Product.fromPartial(e));
      }
    }
    if (object.nextPageToken !== undefined && object.nextPageToken !== null) {
      message.nextPageToken = object.nextPageToken;
    }
    return message;
  },
  toJSON(message: ListProductsResponse): unknown {
    const obj: any = {};
    if (message.products) {
      obj.products = message.products.map(e => e ? Product.toJSON(e) : undefined);
    } else {
      obj.products = [];
    }
    message.nextPageToken !== undefined && (obj.nextPageToken = message.nextPageToken);
    return obj;
  },
};

export const ImageData = {
  encode(message: ImageData, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.src);
    writer.uint32(18).string(message.alt);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): ImageData {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseImageData } as ImageData;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.src = reader.string();
          break;
        case 2:
          message.alt = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): ImageData {
    const message = { ...baseImageData } as ImageData;
    if (object.src !== undefined && object.src !== null) {
      message.src = String(object.src);
    }
    if (object.alt !== undefined && object.alt !== null) {
      message.alt = String(object.alt);
    }
    return message;
  },
  fromPartial(object: DeepPartial<ImageData>): ImageData {
    const message = { ...baseImageData } as ImageData;
    if (object.src !== undefined && object.src !== null) {
      message.src = object.src;
    }
    if (object.alt !== undefined && object.alt !== null) {
      message.alt = object.alt;
    }
    return message;
  },
  toJSON(message: ImageData): unknown {
    const obj: any = {};
    message.src !== undefined && (obj.src = message.src);
    message.alt !== undefined && (obj.alt = message.alt);
    return obj;
  },
};

export const Category = {
  encode(message: Category, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.name);
    writer.uint32(18).string(message.displayName);
    writer.uint32(24).int32(message.id);
    writer.uint32(34).string(message.description);
    if (message.image !== undefined && message.image !== undefined) {
      ImageData.encode(message.image, writer.uint32(42).fork()).ldelim();
    }
    for (const v of message.products) {
      Product.encode(v!, writer.uint32(50).fork()).ldelim();
    }
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): Category {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseCategory } as Category;
    message.products = [];
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.name = reader.string();
          break;
        case 2:
          message.displayName = reader.string();
          break;
        case 3:
          message.id = reader.int32();
          break;
        case 4:
          message.description = reader.string();
          break;
        case 5:
          message.image = ImageData.decode(reader, reader.uint32());
          break;
        case 6:
          message.products.push(Product.decode(reader, reader.uint32()));
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): Category {
    const message = { ...baseCategory } as Category;
    message.products = [];
    if (object.name !== undefined && object.name !== null) {
      message.name = String(object.name);
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = String(object.displayName);
    }
    if (object.id !== undefined && object.id !== null) {
      message.id = Number(object.id);
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = String(object.description);
    }
    if (object.image !== undefined && object.image !== null) {
      message.image = ImageData.fromJSON(object.image);
    }
    if (object.products !== undefined && object.products !== null) {
      for (const e of object.products) {
        message.products.push(Product.fromJSON(e));
      }
    }
    return message;
  },
  fromPartial(object: DeepPartial<Category>): Category {
    const message = { ...baseCategory } as Category;
    message.products = [];
    if (object.name !== undefined && object.name !== null) {
      message.name = object.name;
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = object.displayName;
    }
    if (object.id !== undefined && object.id !== null) {
      message.id = object.id;
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = object.description;
    }
    if (object.image !== undefined && object.image !== null) {
      message.image = ImageData.fromPartial(object.image);
    }
    if (object.products !== undefined && object.products !== null) {
      for (const e of object.products) {
        message.products.push(Product.fromPartial(e));
      }
    }
    return message;
  },
  toJSON(message: Category): unknown {
    const obj: any = {};
    message.name !== undefined && (obj.name = message.name);
    message.displayName !== undefined && (obj.displayName = message.displayName);
    message.id !== undefined && (obj.id = message.id);
    message.description !== undefined && (obj.description = message.description);
    message.image !== undefined && (obj.image = message.image ? ImageData.toJSON(message.image) : undefined);
    if (message.products) {
      obj.products = message.products.map(e => e ? Product.toJSON(e) : undefined);
    } else {
      obj.products = [];
    }
    return obj;
  },
};

export const Color = {
  encode(message: Color, writer: Writer = Writer.create()): Writer {
    writer.uint32(8).int32(message.id);
    writer.uint32(18).string(message.title);
    writer.uint32(26).string(message.displayName);
    writer.uint32(34).string(message.description);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): Color {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseColor } as Color;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.id = reader.int32();
          break;
        case 2:
          message.title = reader.string();
          break;
        case 3:
          message.displayName = reader.string();
          break;
        case 4:
          message.description = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): Color {
    const message = { ...baseColor } as Color;
    if (object.id !== undefined && object.id !== null) {
      message.id = Number(object.id);
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = String(object.title);
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = String(object.displayName);
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = String(object.description);
    }
    return message;
  },
  fromPartial(object: DeepPartial<Color>): Color {
    const message = { ...baseColor } as Color;
    if (object.id !== undefined && object.id !== null) {
      message.id = object.id;
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = object.title;
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = object.displayName;
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = object.description;
    }
    return message;
  },
  toJSON(message: Color): unknown {
    const obj: any = {};
    message.id !== undefined && (obj.id = message.id);
    message.title !== undefined && (obj.title = message.title);
    message.displayName !== undefined && (obj.displayName = message.displayName);
    message.description !== undefined && (obj.description = message.description);
    return obj;
  },
};

export const Fabric = {
  encode(message: Fabric, writer: Writer = Writer.create()): Writer {
    writer.uint32(8).int32(message.id);
    writer.uint32(18).string(message.title);
    writer.uint32(26).string(message.displayName);
    writer.uint32(34).string(message.description);
    if (message.image !== undefined && message.image !== undefined) {
      ImageData.encode(message.image, writer.uint32(42).fork()).ldelim();
    }
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): Fabric {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseFabric } as Fabric;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.id = reader.int32();
          break;
        case 2:
          message.title = reader.string();
          break;
        case 3:
          message.displayName = reader.string();
          break;
        case 4:
          message.description = reader.string();
          break;
        case 5:
          message.image = ImageData.decode(reader, reader.uint32());
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): Fabric {
    const message = { ...baseFabric } as Fabric;
    if (object.id !== undefined && object.id !== null) {
      message.id = Number(object.id);
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = String(object.title);
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = String(object.displayName);
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = String(object.description);
    }
    if (object.image !== undefined && object.image !== null) {
      message.image = ImageData.fromJSON(object.image);
    }
    return message;
  },
  fromPartial(object: DeepPartial<Fabric>): Fabric {
    const message = { ...baseFabric } as Fabric;
    if (object.id !== undefined && object.id !== null) {
      message.id = object.id;
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = object.title;
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = object.displayName;
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = object.description;
    }
    if (object.image !== undefined && object.image !== null) {
      message.image = ImageData.fromPartial(object.image);
    }
    return message;
  },
  toJSON(message: Fabric): unknown {
    const obj: any = {};
    message.id !== undefined && (obj.id = message.id);
    message.title !== undefined && (obj.title = message.title);
    message.displayName !== undefined && (obj.displayName = message.displayName);
    message.description !== undefined && (obj.description = message.description);
    message.image !== undefined && (obj.image = message.image ? ImageData.toJSON(message.image) : undefined);
    return obj;
  },
};

export const Size = {
  encode(message: Size, writer: Writer = Writer.create()): Writer {
    writer.uint32(8).int32(message.id);
    writer.uint32(18).string(message.title);
    writer.uint32(26).string(message.displayName);
    writer.uint32(34).string(message.description);
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): Size {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseSize } as Size;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.id = reader.int32();
          break;
        case 2:
          message.title = reader.string();
          break;
        case 3:
          message.displayName = reader.string();
          break;
        case 4:
          message.description = reader.string();
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): Size {
    const message = { ...baseSize } as Size;
    if (object.id !== undefined && object.id !== null) {
      message.id = Number(object.id);
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = String(object.title);
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = String(object.displayName);
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = String(object.description);
    }
    return message;
  },
  fromPartial(object: DeepPartial<Size>): Size {
    const message = { ...baseSize } as Size;
    if (object.id !== undefined && object.id !== null) {
      message.id = object.id;
    }
    if (object.title !== undefined && object.title !== null) {
      message.title = object.title;
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = object.displayName;
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = object.description;
    }
    return message;
  },
  toJSON(message: Size): unknown {
    const obj: any = {};
    message.id !== undefined && (obj.id = message.id);
    message.title !== undefined && (obj.title = message.title);
    message.displayName !== undefined && (obj.displayName = message.displayName);
    message.description !== undefined && (obj.description = message.description);
    return obj;
  },
};

export const DogBed = {
  encode(message: DogBed, writer: Writer = Writer.create()): Writer {
    if (message.fabric !== undefined && message.fabric !== undefined) {
      Fabric.encode(message.fabric, writer.uint32(10).fork()).ldelim();
    }
    for (const v of message.sizes) {
      Size.encode(v!, writer.uint32(18).fork()).ldelim();
    }
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): DogBed {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseDogBed } as DogBed;
    message.sizes = [];
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.fabric = Fabric.decode(reader, reader.uint32());
          break;
        case 2:
          message.sizes.push(Size.decode(reader, reader.uint32()));
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): DogBed {
    const message = { ...baseDogBed } as DogBed;
    message.sizes = [];
    if (object.fabric !== undefined && object.fabric !== null) {
      message.fabric = Fabric.fromJSON(object.fabric);
    }
    if (object.sizes !== undefined && object.sizes !== null) {
      for (const e of object.sizes) {
        message.sizes.push(Size.fromJSON(e));
      }
    }
    return message;
  },
  fromPartial(object: DeepPartial<DogBed>): DogBed {
    const message = { ...baseDogBed } as DogBed;
    message.sizes = [];
    if (object.fabric !== undefined && object.fabric !== null) {
      message.fabric = Fabric.fromPartial(object.fabric);
    }
    if (object.sizes !== undefined && object.sizes !== null) {
      for (const e of object.sizes) {
        message.sizes.push(Size.fromPartial(e));
      }
    }
    return message;
  },
  toJSON(message: DogBed): unknown {
    const obj: any = {};
    message.fabric !== undefined && (obj.fabric = message.fabric ? Fabric.toJSON(message.fabric) : undefined);
    if (message.sizes) {
      obj.sizes = message.sizes.map(e => e ? Size.toJSON(e) : undefined);
    } else {
      obj.sizes = [];
    }
    return obj;
  },
};

export const Product = {
  encode(message: Product, writer: Writer = Writer.create()): Writer {
    writer.uint32(10).string(message.name);
    writer.uint32(18).string(message.displayName);
    writer.uint32(24).int32(message.id);
    writer.uint32(34).string(message.description);
    if (message.details?.$case === 'dogBed') {
      DogBed.encode(message.details.dogBed, writer.uint32(82).fork()).ldelim();
    }
    return writer;
  },
  decode(input: Uint8Array | Reader, length?: number): Product {
    const reader = input instanceof Uint8Array ? new Reader(input) : input;
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = { ...baseProduct } as Product;
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.name = reader.string();
          break;
        case 2:
          message.displayName = reader.string();
          break;
        case 3:
          message.id = reader.int32();
          break;
        case 4:
          message.description = reader.string();
          break;
        case 10:
          message.details = {$case: 'dogBed', dogBed: DogBed.decode(reader, reader.uint32())};
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
  fromJSON(object: any): Product {
    const message = { ...baseProduct } as Product;
    if (object.name !== undefined && object.name !== null) {
      message.name = String(object.name);
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = String(object.displayName);
    }
    if (object.id !== undefined && object.id !== null) {
      message.id = Number(object.id);
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = String(object.description);
    }
    if (object.dogBed !== undefined && object.dogBed !== null) {
      message.details = {$case: 'dogBed', dogBed: DogBed.fromJSON(object.dogBed)};
    }
    return message;
  },
  fromPartial(object: DeepPartial<Product>): Product {
    const message = { ...baseProduct } as Product;
    if (object.name !== undefined && object.name !== null) {
      message.name = object.name;
    }
    if (object.displayName !== undefined && object.displayName !== null) {
      message.displayName = object.displayName;
    }
    if (object.id !== undefined && object.id !== null) {
      message.id = object.id;
    }
    if (object.description !== undefined && object.description !== null) {
      message.description = object.description;
    }
    if (object.details?.$case === 'dogBed' && object.details?.dogBed !== undefined && object.details?.dogBed !== null) {
      message.details = {$case: 'dogBed', dogBed: DogBed.fromPartial(object.details.dogBed)};
    }
    return message;
  },
  toJSON(message: Product): unknown {
    const obj: any = {};
    message.name !== undefined && (obj.name = message.name);
    message.displayName !== undefined && (obj.displayName = message.displayName);
    message.id !== undefined && (obj.id = message.id);
    message.description !== undefined && (obj.description = message.description);
    message.details?.$case === 'dogBed' && (obj.dogBed = message.details?.dogBed ? DogBed.toJSON(message.details?.dogBed) : undefined);
    return obj;
  },
};

type Builtin = Date | Function | Uint8Array | string | number | undefined;
type DeepPartial<T> = T extends Builtin
  ? T
  : T extends Array<infer U>
  ? Array<DeepPartial<U>>
  : T extends ReadonlyArray<infer U>
  ? ReadonlyArray<DeepPartial<U>>
  : T extends { $case: string }
  ? { [K in keyof Omit<T, '$case'>]?: DeepPartial<T[K]> } & { $case: T['$case'] }
  : T extends {}
  ? { [K in keyof T]?: DeepPartial<T[K]> }
  : Partial<T>;