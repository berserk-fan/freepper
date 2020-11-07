import { createStore, Store } from "redux";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../configs/Data";
import Cookies from "js-cookie";
import { CartState } from "../components/Cart/Cart";

export const cartStateKey = "cartState";

export function readStoredCartState(): CartState {
  const cartCookie = Cookies.get(cartStateKey);
  const parsed = JSON.parse(cartCookie || "{}");
  return parsed.selectedProducts ? parsed : { selectedProducts: [] };
}

export function storeCartState(cartState: CartState): void {
  Cookies.set(cartStateKey, JSON.stringify(cartState));
}

type SET_PRODUCT_COUNT = {
  type: "SET_PRODUCT_COUNT";
  productId: string;
  count: number;
};

type CartUpdate = SET_PRODUCT_COUNT;

function cartReducer(
  { selectedProducts }: CartState = readStoredCartState(),
  action: StoreUpdate
): CartState {
  switch (action.type) {
    case "SET_PRODUCT_COUNT": {
      const res = [...selectedProducts].filter(
        (p) => p.id !== action.productId
      );
      if (action.count >= 1) {
        res.push({ id: action.productId, count: action.count });
      }
      return { selectedProducts: res };
    }
    default:
      return { selectedProducts: selectedProducts };
  }
}

export type StoreState = Partial<{
  cartState: CartState;
}>;

export type StoreUpdate = CartUpdate;
function storeReducer(store: StoreState = {}, action: StoreUpdate): StoreState {
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
