import React from "react";
import { Container } from "@material-ui/core";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";
import CheckoutForm from "../../components/Checkout/CheckoutForm";
import ValueProp from "../../components/Layout/Header/ValueProp";

export default function CheckoutPage() {
  return (
    <>
      <CheckoutHeader />
      <ValueProp />
      <Container maxWidth="sm">
        <CheckoutForm />
      </Container>
    </>
  );
}
