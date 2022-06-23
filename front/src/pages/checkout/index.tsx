import React from "react";
import dynamic from "next/dynamic";
import Skeleton from "@mui/lab/Skeleton/Skeleton";
import Container from "@mui/material/Container";
import CheckoutHeader from "../../components/Layout/Header/CheckoutHeader";

import ValueProp from "../../components/Layout/Header/ValueProp";

const CheckoutForm = dynamic(
  () => import("../../components/Checkout/CheckoutForm"),
  {
    loading: () => <Skeleton variant="rect" width="100%" height="600px" />,
  },
);

export default function CheckoutPage() {
  return (
    <>
      <CheckoutHeader />
      <ValueProp />
      <Container style={{ paddingLeft: 0, paddingRight: 0 }} maxWidth="sm">
        <CheckoutForm />
      </Container>
    </>
  );
}
