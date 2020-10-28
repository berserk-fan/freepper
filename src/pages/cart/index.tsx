import {Product} from "@mamat14/shop-server/shop_model";
import {cartStateKey, shopClient} from "../../store";
import {Box, Container, Grid, Typography} from "@material-ui/core";
import React from "react";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import {GetServerSideProps} from "next";
import Cookie from 'cookie';

export type CartState = {
    productIds: string[]
}

export default function Cart({products}: { products: Product[] }) {
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
                                        products.map(({displayName}) => (
                                                <Box>
                                                    <Typography variant={'h3'}>{displayName}</Typography>
                                                </Box>
                                            )
                                        )
                                    }
                                </Box>
                        }
                    </Grid>
                </Grid>
            </Container>
        </LayoutWithHeader>);
}

function parseCartData(cookieHeader?: string): CartState {
    const defaultCartState = {productIds: []};
    try {
        const actualCartState = JSON.parse(Cookie.parse(cookieHeader || '')[cartStateKey] || '{}');
        return Object.assign(defaultCartState, actualCartState);
    } catch (e) {
        //consider error recovery
        //1.log.error 2.reset-cookies
        return defaultCartState;
    }
}

export const getServerSideProps: GetServerSideProps = async (context) => {
    const cartState = parseCartData(context.req.headers.cookie);
    const productPromises = cartState.productIds
        .map((id) => shopClient.getProduct({name: `categories/beds-category/products/${id}`}));
    const products = await Promise.all(productPromises);
    return {
        props: {
            products: products
        }
    }
};
