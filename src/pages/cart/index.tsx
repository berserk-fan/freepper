import { Container } from "@material-ui/core";
import React from "react";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import Cart from "../../components/Cart/Cart";

export default function CartPage() {
  return (
    <LayoutWithHeader>
      <Container maxWidth={"sm"}>
        <Cart />
      </Container>
    </LayoutWithHeader>
  );
}
