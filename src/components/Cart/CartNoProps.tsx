import React, {useEffect, useState} from "react";
import {Product} from "@mamat14/shop-server/shop_model";
import {requestCartProducts} from "../../pages/cart";
import {cartReducer} from "../../store";
import {Cart} from "./Cart";
import {Box} from "@material-ui/core";

export default function CartNoProps() {
    const [products, setProducts] = useState<Product[]>([]);
    useEffect(() => {
        requestCartProducts(cartReducer.getState())
            .then(setProducts)
    }, []);

    return (<Box padding={1}>
        <Cart products={products}/>
    </Box>)
}
