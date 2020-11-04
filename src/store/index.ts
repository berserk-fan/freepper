import {createStore, Store} from 'redux'
import ShopClient from '@mamat14/shop-server'
import {category, shopProducts} from '../../configs/Data'
import Cookies from 'js-cookie';
import {CartState} from "../components/Cart/Cart";

export const cartStateKey = "cartState";

export function readStoredCartState(): CartState {
    const cartCookie = Cookies.get(cartStateKey);
    console.log(`cookie ${JSON.stringify(cartCookie)}`)
    const parsed = JSON.parse(cartCookie || '{}');
    return parsed.selectedProducts ? parsed : {selectedProducts: []}
}

export function storeCartState(cartState: CartState): void {
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
            const res = [...selectedProducts]
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

export const cartStore = createStore(reducer);

cartStore.subscribe(() => {
    storeCartState(cartStore.getState());
});

export const shopClient = new ShopClient({
    products: shopProducts,
    categories: [category],
    settings: {timeout: 100, errorPercentage: 0}
});
