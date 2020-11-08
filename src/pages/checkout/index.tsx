import React from "react";
import { Container } from "@material-ui/core";
import { parseCartData, requestCartProducts } from "../../components/Cart/Cart";
import { Product } from "@mamat14/shop-server/shop_model";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";
import CheckoutForm from "../../components/Checkout/CheckoutForm";

export type CartProduct = Product & { count: number };

export default function CheckoutPage({
  cartProducts,
}: {
  cartProducts: CartProduct[];
}) {
  return (
    <>
      <CheckoutHeader />
      <Container maxWidth={"sm"}>
        <CheckoutForm cartProducts={cartProducts} />
      </Container>
    </>
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
