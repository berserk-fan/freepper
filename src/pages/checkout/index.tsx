import React from "react";
import { Container } from "@material-ui/core";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";
import {Skeleton} from "@material-ui/lab";
import dynamic from "next/dynamic";
const CheckoutForm = dynamic(
    () => import("../../components/Checkout/CheckoutForm"),
    {loading: () => <Skeleton variant={"rect"} width={"100%"} height={"600px"}/>}
);

import ValueProp from "../../components/Layout/Header/ValueProp";

export default function CheckoutPage() {
  return (
    <>
      <CheckoutHeader />
      <ValueProp />
      <Container style={{paddingLeft: 0, paddingRight: 0}} maxWidth="sm">
        <CheckoutForm />
      </Container>
    </>
  );
}
