import {Product} from "@mamat14/shop-server/shop_model";
import {cartStateKey, shopClient} from "../../store";
import {
    Box,
    Button, Card, CardActions,
    CardContent,
    CardHeader,
    Container,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Divider,
    Grid,
    Typography
} from "@material-ui/core";
import React from "react";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import {GetServerSideProps} from "next";
import Cookie from 'cookie';
import Image from 'next/image'

export type CartState = {
    showCart: boolean,
    productIds: string[]
}

function getAdditionalInfo({details}: Product) {
    switch (details.$case) {
        case "dogBed":
            const size= details.dogBed.sizes.find((s) => s.id == details.dogBed.sizeId);
            return (<div>
                <Typography display={'inline'} variant={'caption'}>Размер: </Typography>
                <Typography display={'inline'} variant={'h6'}>
                    {size.displayName}
                </Typography>
            </div>)
    }
}

function CartItem(props: { product: Product }) {
    const {displayName, price, image, details} = props.product;
    switch (details.$case) {
        case "dogBed":
    }
    return (
        <div className={"m-4 grid grid-flow-col auto-cols-auto gap-4"}>
            <div>
                <Image width={100} height={100} src={image.src} alt={image.alt}/>
            </div>
            <div className={"col-span-3 flex flex-col justify-between"}>
                <Typography variant={'h5'}>{displayName}</Typography>
                <div className={"flex flex-row justify-between"}>
                    <div>
                        {getAdditionalInfo(props.product)}
                    </div>
                    <Typography align='right' color='primary' variant={'h5'}>{price.price} ₴</Typography>
                </div>
            </div>
        </div>
    );
}

export default function Cart({products}: { products: Product[] }) {
    const totalPrice = products.map(p => p.price.price).reduce((a, b) => a + b, 0);
    return (
        <LayoutWithHeader>
            <Container maxWidth={"sm"}>
                <Card>
                    <CardHeader title={'Корзина'}/>
                    <Divider/>
                    <CardContent>
                        {products.length === 0
                            ? <Typography variant={'h2'}>Корзина пуста</Typography>
                            : products.map(product => (
                                <>
                                    <CartItem product={product}/>
                                    <Divider className={"last:hidden"} light/>
                                </>))
                        }
                    </CardContent>
                    <CardActions className={"flex flex-row items-center"}>
                        <Box border={2} borderColor={'primary.main'} bgcolor={'primary.light'} className={"flex flex-row items-center rounded p-6"} style={{marginLeft: 'auto'}}>
                            <div className={"mr-6"}>
                                <Typography variant='h5' align='center'>{totalPrice} ₴</Typography>
                            </div>
                            <Button color={'primary'} variant='contained' size='large'>Оформить заказ</Button>
                        </Box>
                    </CardActions>
                </Card>
            </Container>
        </LayoutWithHeader>
    );
}

function parseCartData(cookieHeader?: string): Omit<CartState, "showCart"> {
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
