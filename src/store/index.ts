import {createStore, Store} from 'redux'
import ShopClient from '@mamat14/shop-server'
import {category, shopProducts} from '../../configs/Data'
import {CartState} from "../pages/cart";
import Cookies from 'js-cookie';

/**
 * This is a reducer, a pure function with (state, action) => state signature.
 * It describes how an action transforms the state into the next state.
 *
 * The shape of the state is up to you: it can be a primitive, an array, an object,
 * or even an Immutable.js data structure. The only important part is that you should
 * not mutate the state object, but return a new object if the state changes.
 *
 * In this example, we use a `switch` statement and strings, but you can use a helper that
 * follows a different convention (such as function maps) if it makes sense for your
 * project.
 */
type ADD_PRODUCT = {
    type: "ADD_PRODUCT",
    productId: string
}
type DELETE_PRODUCT = {
    type: "DELETE_PRODUCT",
    productId: string
}

type CartUpdate = ADD_PRODUCT | DELETE_PRODUCT;

export const cartStateKey = "cartState";
export const initialCartState = '{"productIds": []}';

function readStoredCartState(): CartState {
    return JSON.parse(Cookies.get(cartStateKey) || initialCartState)
}
function storeCartState(cartState: CartState): void {
    Cookies.set(cartStateKey, JSON.stringify(cartState));
}

function selectedProducts({productIds}: CartState = readStoredCartState(), action: CartUpdate): CartState {
    switch (action.type) {
        case 'ADD_PRODUCT': {
            const newIds = Array.from(productIds.values());
            newIds.push(action.productId);
            return {productIds: newIds};
        }
        case 'DELETE_PRODUCT': {
            const newIds = new Set(productIds);
            newIds.delete(action.productId);
            return {productIds: Array.from(newIds.values())};
        }
        default:
            return {productIds: productIds}
    }
}

export const CART: Store<CartState, CartUpdate> = createStore(selectedProducts);

CART.subscribe(() => {
    storeCartState(CART.getState());
});

export const SHOP_CLIENT = new ShopClient({products: shopProducts, categories: [category]});
