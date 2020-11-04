import {Container} from "@material-ui/core";
import React from "react";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import {GetServerSideProps} from "next";
import {
    CartSSProps,
    parseCartData,
    requestCartProducts
} from "../../components/Cart/Cart";
import CartNoProps from "../../components/Cart/CartNoProps";

export default function CartPage({cartProducts}: CartSSProps) {
    return (
        <LayoutWithHeader>
            <Container maxWidth={"sm"}>
                <CartNoProps initialProducts={cartProducts}/>
            </Container>
        </LayoutWithHeader>
    );
}

export const getServerSideProps: GetServerSideProps<CartSSProps, NodeJS.Dict<string>> = async (context) => {
    const cartState = parseCartData(context.req.headers.cookie);
    const cartSSProps =  {cartProducts: await requestCartProducts(cartState)};
    return {props: cartSSProps};
};
