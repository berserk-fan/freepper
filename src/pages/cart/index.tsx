import React from "react";
import Container from "@material-ui/core/Container";
import LayoutWithHeaderAndFooter from "../../components/Layout/LayoutWithHeaderAndFooter";
import Cart from "../../components/Cart/Cart";

export default function CartPage() {
  return (
    <LayoutWithHeaderAndFooter>
      <Container maxWidth="sm">
        <Cart />
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}
