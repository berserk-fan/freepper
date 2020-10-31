import React, {useEffect, useState} from "react";
import {Product} from "@mamat14/shop-server/shop_model";
import {requestCartProducts} from "../../pages/cart";
import {cartReducer} from "../../store";
import {Cart} from "./Cart";
import retry from 'promise-retry';

type CartNoPropsState = "PENDING" | "COMPLETED" | "FAILED"
export default function CartNoProps({initialProducts}: {initialProducts?: Product[]}) {
    if(!initialProducts) {
        initialProducts = []
    }
    const [products, setProducts] = useState<Product[]>(initialProducts);
    const [state, setState] = useState<CartNoPropsState>("PENDING");
    cartReducer.subscribe(() => {
        setState("PENDING");
    });

    useEffect(() => {
        if (state === "PENDING") {
            retry((retry, number) =>
                requestCartProducts(cartReducer.getState())
                    .then((products) => {
                        setProducts(products);
                        setState("COMPLETED")
                    })
                    .catch(retry)
            ).catch((err) => {
                console.error(err);
                setState("FAILED")
            })
        }
    });

    return (<Cart products={products}/>)
}
