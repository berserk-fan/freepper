import { Product } from "@mamat14/shop-server/shop_model";

export type BriefProduct = Omit<Product, "details">;
