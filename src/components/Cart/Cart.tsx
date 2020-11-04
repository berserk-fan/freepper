import {makeStyles} from "@material-ui/styles";
import theme from "../../theme";
import {Product} from "@mamat14/shop-server/shop_model";
import {cartStateKey, shopClient} from "../../store";
import React from "react";
import {Box, Button, Typography} from "@material-ui/core";
import CartItem from "./CartItem";
import {connect} from "react-redux";
import Cookie from 'cookie'
import {CartProduct} from "../../pages/checkout";

export type CartState = {
    selectedProducts: { id: string, count: number }[]
}

const useStyles = makeStyles(({
    textWrapper: {
        width: '100%',
        display: 'flex',
        justifyContent: 'space-between',
        paddingLeft: theme.spacing(1),
        paddingTop: theme.spacing(1),
        paddingBottom: theme.spacing(1),
        [theme.breakpoints.up('sm')]: {
            width: 'auto',
            marginRight: theme.spacing(1)
        }
    },
    mainButtonContainer: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0,
        flexGrow: 1,
        flexDirection: 'column',
        borderWidth: 2,
        borderColor: theme.palette.primary.main,
        backgroundColor: theme.palette.primary.light,
        padding: theme.spacing(1),
        [theme.breakpoints.up('sm')]: {
            flexDirection: 'row',
            padding: theme.spacing(3),
            flexGrow: 0,
            flexShrink: 1,
        }
    },
    mainButton: {
        width: '100%',
        flexGrow: 1,
        [theme.breakpoints.up('sm')]: {
            width: 'auto',
            flexGrow: 0,
            flexShrink: 1,
        }
    },
    prePriceText: {
        [theme.breakpoints.up('sm')]: {
            display: 'none'
        }
    }
}));

const cart = function Cart({cartProducts, totalPrice}: CartSSProps & { totalPrice: number }) {
    const classes = useStyles();
    const productsList = Object.values(cartProducts).map(p => p[1]);
    return <div>
        <Box marginTop={2}>
            {productsList.length === 0
                ? <Typography variant={'h2'}>Корзина пуста</Typography>
                : productsList.map(product => (
                    <Box key={product.id} marginY={1}>
                        <CartItem product={product}/>
                    </Box>))
            }
        </Box>
        <Box marginTop={2} className={`flex justify-end items-center`}>
            <div className={`rounded ${classes.mainButtonContainer}`}>
                <div className={classes.textWrapper}>
                    <Typography variant={'h5'} classes={{root: classes.prePriceText}}>Итого</Typography>
                    <Typography variant='h5'>{totalPrice}₴</Typography>
                </div>
                <Button classes={{root: classes.mainButton}} color={'primary'} variant='contained' size='large'>
                    Оформить заказ
                </Button>
            </div>
        </Box>
    </div>
};

export function calcTotalPrice(products: Record<string, Product>, cartState: CartState) {
    return cartState.selectedProducts
        .map(p => p.count * (products[p.id]?.price?.price || 0))
        .reduce((a, b) => a + b, 0)
}


export function mapStateToCartProps(state: CartState, {products}: { products: Record<string, Product> }) {
    return {
        totalPrice: calcTotalPrice(products, state)
    }
}

export default connect(mapStateToCartProps)(cart)

export function parseCartData(cookieHeader?: string): CartState {
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

export async function requestCartProducts(cartState: CartState): Promise<Record<string, CartProduct>> {
    const productPromises = cartState.selectedProducts
        .map(async (p) => ({
            ...(await shopClient.getProduct({name: `categories/beds-category/products/${p.id}`})),
            ...{count: p.count}
        }));
    return (await Promise.all(productPromises))
        .reduce((res, product) => ({...res, ...{[product.id]: product}}), {});
}

export type CartSSProps = { cartProducts: Record<string, CartProduct> }
