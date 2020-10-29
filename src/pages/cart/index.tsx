import {Product} from "@mamat14/shop-server/shop_model";
import {cartStateKey, shopClient} from "../../store";
import {
    Box,
    Button,
    Container,
    Divider,
    Typography
} from "@material-ui/core";
import React from "react";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import {GetServerSideProps} from "next";
import Cookie from 'cookie';
import CartItem from "../../components/Cart/CartItem";

export type CartState = {
    selectedProducts: {id: string, count: number}[]
}

export default function Cart({products}: { products: Product[] }) {
    const totalPrice = products.map(p => p.price.price).reduce((a, b) => a + b, 0);
    return (
        <LayoutWithHeader>
            <Container maxWidth={"sm"}>
                <div>
                    <Box paddingBottom={1}>
                        <Typography variant={'h4'} >Корзина</Typography>
                    </Box>
                    <Divider/>
                    <Box marginTop={2}>
                        {products.length === 0
                            ? <Typography variant={'h2'}>Корзина пуста</Typography>
                            : products.map(product => (
                                <Box marginY={1}>
                                    <CartItem product={product}/>
                                </Box>))
                        }
                    </Box>
                    <Box padding={2} className={"flex flex-row items-center"}>
                        <Box border={2} borderColor={'primary.main'} bgcolor={'primary.light'} className={"flex flex-row items-center rounded p-6"} style={{marginLeft: 'auto'}}>
                            <div className={"mr-6"}>
                                <Typography variant='h4' align='center'>{totalPrice}₴</Typography>
                            </div>
                            <Button color={'primary'} variant='contained' size='large'>Оформить заказ</Button>
                        </Box>
                    </Box>
                </div>
            </Container>
        </LayoutWithHeader>
    );
}

function parseCartData(cookieHeader?: string): CartState {
    const defaultCartState = {selectedProducts: []};
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
    console.log(JSON.stringify(cartState));
    const productPromises = cartState.selectedProducts
        .map((p) => shopClient.getProduct({name: `categories/beds-category/products/${p.id}`}));
    const products = await Promise.all(productPromises);
    return {
        props: {
            products: products
        }
    }
};
