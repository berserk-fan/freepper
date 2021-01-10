import { createStore, Store } from "redux";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../configs/Data";
import Cookies from "js-cookie";
import { CartState } from "../components/Cart/Cart";
import { CartProduct } from "../pages/checkout";
import { Product } from "@mamat14/shop-server/shop_model";

export const cartStateKey = "cartState";

export function readStoredCartState(): CartState {
  const cartCookie = Cookies.get(cartStateKey);
  const parsed = JSON.parse(cartCookie || "{}");
  return parsed.selectedProducts
    ? parsed
    : { selectedProducts: [], total: 0, cartSize: 0 };
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
  const { selectedProducts, total, cartSize } = cartState;
  switch (action.type) {
    case "SET_PRODUCT_COUNT": {
      const toSet = selectedProducts[action.productId];
      if (!toSet) {
        return cartState;
      }
      const change = action.count - toSet.count;
      return {
        total: total + toSet.price.price * change,
        cartSize: cartSize + change,
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
      return {
        total: total + action.product.price.price,
        cartSize: cartSize + 1,
        selectedProducts: {
          ...selectedProducts,
          ...{ [action.product.id]: { ...action.product, ...{ count: 1 } } },
        },
      };
    }
    case "DELETE_PRODUCT": {
      const toDelete = selectedProducts[action.productId];
      if (!toDelete) {
        return cartState;
      }
      return {
        total: total - toDelete.price.price * toDelete.count,
        cartSize: cartSize - toDelete.count,
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
