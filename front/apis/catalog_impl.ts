import {
  Catalog,
  Category,
  GetCategoryRequest,
  GetProductRequest,
  ListProductsRequest,
  ListProductsResponse,
  Product,
} from "./catalog";

type StaticShopServiceProps = {
  categories: Category[];
  products: Product[];
  settings?: { timeout: number; errorPercentage: number };
};

export default class StaticCatalog implements Catalog {
  private timeout = this.props.settings?.timeout || 0;

  private errorPercentage = this.props.settings?.errorPercentage || 0;

  private static testErrorC = () => new Error("test error");

  private wrapErrorTimeout<T>(t: T): Promise<T> {
    const toError = Math.random() < this.errorPercentage / 100;
    if (this.timeout === 0) {
      return toError
        ? Promise.reject(StaticCatalog.testErrorC())
        : Promise.resolve(t);
    }
    return new Promise((resolve, reject) =>
      setTimeout(
        () => (toError ? reject(StaticCatalog.testErrorC()) : resolve(t)),
        this.timeout,
      ),
    );
  }

  constructor(private props: StaticShopServiceProps) {}

  getCategory(request: GetCategoryRequest): Promise<Category> {
    const res = this.props.categories.find((c) => c.name === request.name);
    return this.wrapErrorTimeout(res);
  }

  getProduct(request: GetProductRequest): Promise<Product> {
    const res = this.props.products.find((p) => p.name === request.name);
    return this.wrapErrorTimeout(res);
  }

  listProducts(request: ListProductsRequest): Promise<ListProductsResponse> {
    let offset: number;
    if (request.pageToken.length === 0) {
      offset = 0;
    } else {
      offset = Number.parseInt(atob(request.pageToken), 10);
    }
    const end = offset + request.pageSize;
    const res = {
      products: this.props.products.slice(offset, end),
      nextPageToken: Buffer.from(end.toString()).toString("base64"),
    };
    return this.wrapErrorTimeout(res);
  }
}
