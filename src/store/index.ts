import {createStore, Store} from 'redux'
import ShopClient from '@mamat14/shop-server'
import {category, shopProducts} from '../../configs/Data'
import {CartState} from "../pages/cart";
import Cookies from 'js-cookie';

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

function readStoredCartState(): CartState {
    const cartCookie = Cookies.get(cartStateKey);
    return cartCookie ? JSON.parse(cartCookie) : {productIds: []}
}

function storeCartState(cartState: CartState): void {
    Cookies.set(cartStateKey, JSON.stringify(cartState));
}

function reducer({productIds}: CartState = readStoredCartState(), action: CartUpdate): CartState {
    switch (action.type) {
        case 'ADD_PRODUCT': {
            return {productIds: [...productIds, action.productId]};
        }
        case 'DELETE_PRODUCT': {
            const newIds = new Set(productIds);
            newIds.delete(action.productId);
            return {productIds: [...newIds]};
        }
        default:
            return {productIds}
    }
}

export const CART: Store<CartState, CartUpdate> = createStore(reducer);

CART.subscribe(() => {
    storeCartState(CART.getState());
});

export const SHOP_CLIENT = new ShopClient({products: shopProducts, categories: [category]});
