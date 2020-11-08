import React from "react";
import { Container } from "@material-ui/core";
import Checkout from "../../components/Checkout/CheckoutForm";
import { parseCartData, requestCartProducts } from "../../components/Cart/Cart";
import { Product } from "@mamat14/shop-server/shop_model";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";

export type CartProduct = Product & { count: number };

export default function CheckoutPage(props: { cartProducts: CartProduct[] }) {
  return (
    <LayoutWithHeader>
      <Container maxWidth={"md"}>
        <Checkout {...props} />
      </Container>
    </LayoutWithHeader>
  );
}

export const getServerSideProps = async (context) => {
  const state = parseCartData(context.req.headers.cookie);
  const products = await requestCartProducts(state);
  const cartProducts = state.selectedProducts.map(({ id, count }) => ({
    ...products[id],
    ...{ count },
  }));
  return { props: { cartProducts } };
};
