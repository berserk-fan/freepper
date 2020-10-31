import {makeStyles} from "@material-ui/styles";
import theme from "../../theme";
import {Product} from "@mamat14/shop-server/shop_model";
import {cartReducer} from "../../store";
import React, {useEffect, useState} from "react";
import {Box, Button, Divider, Typography} from "@material-ui/core";
import CartItem from "./CartItem";
import {connect} from "react-redux";

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

const cart = function Cart({products, totalPrice}: { products: Map<string, Product>, totalPrice: number | undefined }) {
    const classes = useStyles();
    const productsList = [...products.values()];
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

function calcTotalPrice(productsMap: Map<string, Product>, cartState: CartState) {
    return cartState.selectedProducts
        .map(p => p.count * (productsMap.get(p.id)?.price?.price || 0))
        .reduce((a, b) => a + b, 0)
}


function mapStateToProps(state: CartState, {products}: {products: Map<string, Product>}) {
    return {
        totalPrice: calcTotalPrice(products, state)
    }
}

export default connect(mapStateToProps)(cart)
