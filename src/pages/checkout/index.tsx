import React from "react";
import { Container } from "@material-ui/core";
import { Product } from "@mamat14/shop-server/shop_model";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";
import CheckoutForm from "../../components/Checkout/CheckoutForm";
import ValueProp from "../../components/Layout/Header/ValueProp";

export type CartProduct = Product & { count: number };

export default function CheckoutPage() {
  return (
    <>
      <CheckoutHeader />
      <ValueProp />
      <Container maxWidth={"sm"}>
        <CheckoutForm />
      </Container>
    </>
  );
}
