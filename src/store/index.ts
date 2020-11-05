import { createStore, Store } from "redux";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../configs/Data";
import Cookies from "js-cookie";
import { CartState } from "../components/Cart/Cart";
import { DeliveryOption, Order } from "../order-model";

export const cartStateKey = "cartState";

export function readStoredCartState(): CartState {
  const cartCookie = Cookies.get(cartStateKey);
  console.log(`cookie ${JSON.stringify(cartCookie)}`);
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
  action: CartUpdate
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

type UPDATE_FULL_NAME = {
  type: "UPDATE_FULL_NAME";
  fullName: string;
};

type UPDATE_DELIVERY_ADDRESS = {
  type: "UPDATE_SHIPPING_ADDRESS";
  address: string;
};

type UPDATE_DELIVERY_OPTION = {
  type: "UPDATE_DELIVERY_OPTION";
  deliveryOption: DeliveryOption;
};

type OrderUpdate =
  | UPDATE_FULL_NAME
  | UPDATE_DELIVERY_ADDRESS
  | UPDATE_DELIVERY_OPTION;

function orderReducer(
  order: Partial<Order>,
  action: OrderUpdate
): Partial<Order> {
  switch (action.type) {
    case "UPDATE_FULL_NAME":
      return {
        ...order,
        ...{
          deliveryDetails: {
            ...order.deliveryDetails,
            ...{ fullName: action.fullName },
          },
        },
      };
    default:
      return order;
  }
}

export const cartStore = createStore(cartReducer);

cartStore.subscribe(() => {
  storeCartState(cartStore.getState());
});

export const shopClient = new ShopClient({
  products: shopProducts,
  categories: [category],
  settings: { timeout: 100, errorPercentage: 0 },
});
