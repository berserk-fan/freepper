import React, {useEffect, useState} from "react";
import {Product} from "@mamat14/shop-server/shop_model";
import {requestCartProducts} from "../../pages/cart";
import {cartReducer} from "../../store";
import Cart, {CartState} from "./Cart";
import retry from 'promise-retry';

type CartNoPropsState = "PENDING" | "COMPLETED" | "FAILED"
export default function CartNoProps({initialProducts = new Map()}: {initialProducts?: Map<string, Product>}) {
    const [products, setProducts] = useState<Map<string, Product>>(initialProducts);
    const [state, setState] = useState<CartNoPropsState>("PENDING");

    function fetchProducts() {
        if (state === "PENDING") {
            console.log('fetching');
            retry((retry) =>
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
    }

    cartReducer.subscribe(() => {
        setState("PENDING");
        fetchProducts()
    });

    useEffect(() => {
        fetchProducts()
    }, []);

    return (<Cart products={products}/>)
}
