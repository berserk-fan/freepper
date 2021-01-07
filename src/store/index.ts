import { createStore, Store } from "redux";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../configs/Data";
import Cookies from "js-cookie";
import { CartState } from "../components/Cart/Cart";
import { CartProduct } from "../pages/checkout";
import exp from "constants";
import { Product } from "@mamat14/shop-server/shop_model";

export const cartStateKey = "cartState";

export function readStoredCartState(): CartState {
  const cartCookie = Cookies.get(cartStateKey);
  const parsed = JSON.parse(cartCookie || "{}");
  return parsed.selectedProducts
    ? parsed
    : { selectedProducts: [], total: 0, size: 0 };
}

export function storeCartState(cartState: CartState): void {
  Cookies.set(cartStateKey, JSON.stringify(cartState));
}

export function SetProductCountAction(productId: string, count: number) {
  return {
    type: "SET_PRODUCT_COUNT",
    productId,
    count,
  };
}

type SET_PRODUCT_COUNT = {
  type: "SET_PRODUCT_COUNT";
  productId: string;
  count: number;
};

type ADD_PRODUCT = {
  type: "ADD_PRODUCT";
  product: Product;
};

type DELETE_PRODUCT = {
  type: "DELETE_PRODUCT";
  productId: string;
};

type CartUpdate = SET_PRODUCT_COUNT | ADD_PRODUCT | DELETE_PRODUCT;

export function addProductAction(product: CartProduct): ADD_PRODUCT {
  return {
    type: "ADD_PRODUCT",
    product: product,
  };
}

export function setProductCountAction(
  productId: string,
  count: number
): SET_PRODUCT_COUNT {
  return {
    type: "SET_PRODUCT_COUNT",
    productId,
    count,
  };
}

export function deleteProductAction(productId: string): DELETE_PRODUCT {
  return {
    type: "DELETE_PRODUCT",
    productId,
  };
}

function cartReducer(cartState: CartState, action: StoreUpdate): CartState {
  const { selectedProducts, total, size } = cartState;
  switch (action.type) {
    case "SET_PRODUCT_COUNT": {
      return {
        total:
          total + selectedProducts[action.productId].price.price * action.count,
        size: size - selectedProducts[action.productId].count + action.count,
        selectedProducts: {
          ...selectedProducts,
          ...{
            [action.productId]: {
              ...selectedProducts[action.productId],
              ...{ count: action.count },
            },
          },
        },
      };
    }
    case "ADD_PRODUCT": {
      if (selectedProducts[action.product.id]) {
        return cartState;
      }
      return {
        total: total + action.product.price.price,
        size: size + 1,
        selectedProducts: {
          ...selectedProducts,
          ...{ [action.product.id]: { ...action.product, ...{ count: 1 } } },
        },
      };
    }
    case "DELETE_PRODUCT": {
      if (!selectedProducts[action.productId]) {
        return cartState;
      }
      return {
        total:
          total -
          selectedProducts[action.productId].price.price *
            selectedProducts[action.productId].count,
        size: size - selectedProducts[action.productId].count,
        selectedProducts: Object.fromEntries(
          Object.values(selectedProducts)
            .filter((p) => p.id !== action.productId)
            .map((p) => [p.id, p])
        ),
      };
    }
    default:
      return cartState;
  }
}

export type StoreState = Partial<{
  cartState: CartState;
}>;

export type StoreUpdate = CartUpdate;
const initialStoreState = {
  cartState: readStoredCartState(),
};
function storeReducer(
  store: StoreState = initialStoreState,
  action: StoreUpdate
): StoreState {
  return {
    cartState: cartReducer(store.cartState, action),
  };
}

export const store = createStore(storeReducer);

store.subscribe(() => {
  storeCartState(store.getState().cartState);
});

export const shopClient = new ShopClient({
  products: shopProducts,
  categories: [category],
  settings: { timeout: 100, errorPercentage: 0 },
});
