import {ShopService} from "./shop_service";
import {
    Category,
    GetCategoryRequest,
    GetProductRequest,
    ListProductsRequest,
    ListProductsResponse,
    Product
} from "./shop_model";

export default class ShopServiceImpl implements ShopService {
    getCategory(request: GetCategoryRequest): Promise<Category> {
        return undefined;
    }

    getProduct(request: GetProductRequest): Promise<Product> {
        return undefined;
    }

    listProducts(request: ListProductsRequest): Promise<ListProductsResponse> {
        return undefined;
    }
}
