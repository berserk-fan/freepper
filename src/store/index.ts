import {createStore, Store} from 'redux'
import ShopClient from '@mamat14/shop-server'
import {category, shopProducts} from '../../configs/Data'
import Cookies from 'js-cookie';
import {CartState} from "../components/Cart/Cart";

export const cartStateKey = "cartState";

function readStoredCartState(): CartState {
    const cartCookie = Cookies.get(cartStateKey);
    const parsed = JSON.parse(cartCookie || '{}');
    if(parsed.selectedProducts) {
        return parsed
    }
    return {selectedProducts: []};
}

function storeCartState(cartState: CartState): void {
    Cookies.set(cartStateKey, JSON.stringify(cartState));
}

type SET_PRODUCT_COUNT = {
    type: "SET_PRODUCT_COUNT",
    productId: string,
    count: number
}

type CartUpdate = SET_PRODUCT_COUNT

function reducer({selectedProducts}: CartState = readStoredCartState(), action: CartUpdate): CartState {
    switch (action.type) {
        case 'SET_PRODUCT_COUNT': {
            let res = [...selectedProducts]
                .filter(p => p.id !== action.productId);
            if (action.count >= 1) {
                res.push({id: action.productId, count: action.count});
            }
            return {selectedProducts: res}
        }
        default:
            return {selectedProducts: selectedProducts}
    }
}

export const cartReducer = createStore(reducer);

export const CART = {
    setProductCount: function (productId: string, count: number): void {
        cartReducer.dispatch({type: 'SET_PRODUCT_COUNT', count: count, productId: productId})
    },
    getProductCount: function (productId: string): number {
        return cartReducer.getState().selectedProducts
                .find(p => p.id === productId)?.count || 0;
    }
};

cartReducer.subscribe(() => {
    storeCartState(cartReducer.getState());
});

export const shopClient = new ShopClient({products: shopProducts, categories: [category]});
