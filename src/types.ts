import {Product} from "@mamat14/shop-server/shop_model";

type BriefProduct = Omit<Product, "details">;
