import { createStore } from "redux";
import ShopClient from "@mamat14/shop-server";
import { Product } from "@mamat14/shop-server/shop_model";
import { categories, shopProducts } from "../../configs/Data";

const initialState: StoreState = {
  cartState: {
    cartSize: 0,
    total: 0,
    selectedProducts: {},
  },
};

export const loadState: () => StoreState = () => {
  try {
    const serializedState = localStorage.getItem("state");
    if (!serializedState) {
      return initialState;
    }
    return JSON.parse(serializedState);
  } catch (err) {
    return initialState;
  }
};

export const saveState = (state) => {
  try {
    const serializedState = JSON.stringify(state);
    localStorage.setItem("state", serializedState);
  } catch (err) {
    console.error(err);
  }
};

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

type CLEAR_CART = {
  type: "CLEAR_CART";
};

type CartUpdate = SET_PRODUCT_COUNT | ADD_PRODUCT | DELETE_PRODUCT | CLEAR_CART;

export type CartProduct = Product & { count: number };

export function addProductAction(product: CartProduct): ADD_PRODUCT {
  return {
    type: "ADD_PRODUCT",
    product,
  };
}

export function setProductCountAction(
  productId: string,
  count: number,
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

export function clearCartAction(): CLEAR_CART {
  return {
    type: "CLEAR_CART",
  };
}

export type CartState = {
  cartSize: number;
  total: number;
  selectedProducts: Record<string, CartProduct>;
};

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
            .map((p) => [p.id, p]),
        ),
      };
    }
    case "CLEAR_CART": {
      return {
        cartSize: 0,
        total: 0,
        selectedProducts: {},
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
const initialStoreState = loadState();

function storeReducer(
  store: StoreState = initialStoreState,
  action: StoreUpdate,
): StoreState {
  return {
    cartState: cartReducer(store.cartState, action),
  };
}

export const store = createStore(storeReducer);

store.subscribe(() => {
  saveState(store.getState());
});

export const shopClient = new ShopClient({
  products: shopProducts,
  categories,
  settings: { timeout: 100, errorPercentage: 0 },
});
