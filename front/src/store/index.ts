import { createStore } from "redux";
import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";
import { Product } from "apis/product.pb";
import { Model } from "apis/model.pb";
import { getCurrentPrice } from "../commons/utils";

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
  cartProduct: CartProduct;
};

type DELETE_PRODUCT = {
  type: "DELETE_PRODUCT";
  productId: string;
};

type CLEAR_CART = {
  type: "CLEAR_CART";
};

type CartUpdate = SET_PRODUCT_COUNT | ADD_PRODUCT | DELETE_PRODUCT | CLEAR_CART;

export type CartProduct = { model: Model; product: Product; count: number };

export function addProductAction(product: CartProduct): ADD_PRODUCT {
  return {
    type: "ADD_PRODUCT",
    cartProduct: product,
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
        total: total + getCurrentPrice(toSet.product.price).amount * change,
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
        total: total + getCurrentPrice(action.cartProduct.product.price).amount,
        cartSize: cartSize + 1,
        selectedProducts: {
          ...selectedProducts,
          ...{
            [action.cartProduct.product.uid]: {
              ...action.cartProduct,
              ...{ count: 1 },
            },
          },
        },
      };
    }
    case "DELETE_PRODUCT": {
      const toDelete = selectedProducts[action.productId];
      if (!toDelete) {
        return cartState;
      }
      return {
        total:
          total -
          getCurrentPrice(toDelete.product.price).amount * toDelete.count,
        cartSize: cartSize - toDelete.count,
        selectedProducts: Object.fromEntries(
          Object.values(selectedProducts)
            .filter((p) => p.product.uid !== action.productId)
            .map((p) => [p.product.uid, p]),
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

export const shopNode = new CatalogClientImpl(
  new GrpcWebImpl("http://localhost:8080", {
    transport: NodeHttpTransport(),
    debug: false,
  }),
);

export const shopWeb = new CatalogClientImpl(
  new GrpcWebImpl("http://localhost:8080", {
    debug: false,
  }),
);
