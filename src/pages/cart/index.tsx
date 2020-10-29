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
import {makeStyles} from "@material-ui/styles";
import theme from "../../theme";

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

export default function Cart({products}: { products: Product[] }) {
    const classes = useStyles();

    const totalPrice = products.map(p => p.price.price).reduce((a, b) => a + b, 0);
    return (
        <LayoutWithHeader>
            <Container maxWidth={"sm"}>
                <div>
                    <Box paddingBottom={1}>
                        <Typography variant={'h4'}>Корзина</Typography>
                    </Box>
                    <Divider/>
                    <Box marginTop={2}>
                        {products.length === 0
                            ? <Typography variant={'h2'}>Корзина пуста</Typography>
                            : products.map(product => (
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
