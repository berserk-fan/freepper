import {Product} from "@mamat14/shop-server/shop_model";
import {CART, cartStateKey, initialCartState, SHOP_CLIENT} from "../../store";
import {Box, Container, Grid, Typography} from "@material-ui/core";
import React, {useEffect, useState} from "react";
import CartItem from "../../components/Cart/CartItem";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import {GetServerSideProps} from "next";
import Cookie from 'cookie';
export type CartState = {
    productIds: string[]
}

function parseCartData(cookieHeader?: string): CartState {
    console.log("COOKIE: " + cookieHeader);
    return JSON.parse(Cookie.parse(cookieHeader || `${cartStateKey}=${initialCartState}`)[cartStateKey]);
}

export const getServerSideProps: GetServerSideProps = async (context) => {
    const cartState = parseCartData(context.req.headers.cookie);
    const productPromises = cartState.productIds
        .map((id) => SHOP_CLIENT.getProduct({name: `categories/beds-category/products/${id}`}));
    const products = await Promise.all(productPromises);
    return {
        props: {
            products: products
        }
    }
};

export default function Cart({products}: {products: Product[]}) {
    return (
        <LayoutWithHeader>
            <Container>
                <Typography variant={'h1'}>Корзина</Typography>
                <Grid container>
                    <Grid item>
                        {
                            products.length === 0
                                ? <Typography variant={'h2'}>Корзина пуста</Typography>
                                : <Box>
                                    <Typography variant={'h2'}>Товары:</Typography>
                                    {
                                        products.map(p => <CartItem key={p.id} product={p}/>)
                                    }
                                </Box>
                        }
                    </Grid>
                </Grid>
            </Container>
        </LayoutWithHeader>);
}
