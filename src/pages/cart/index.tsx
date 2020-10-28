import {Product} from "@mamat14/shop-server/shop_model";
import {cartStateKey, SHOP_CLIENT} from "../../store";
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

export const getServerSideProps: GetServerSideProps = async (context) => {
    const {productIds} = parseCartData(context.req.headers.cookie);
    const products = await Promise.all(
        productIds.map(id => SHOP_CLIENT.getProduct({name: `categories/beds-category/products/${id}`}))
    );
    return {
        props: {products}
    }
};

function parseCartData(cookieHeader?: string): CartState {
    return JSON.parse(Cookie.parse(cookieHeader || '')[cartStateKey]) || {productIds: []};
}
