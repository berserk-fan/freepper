import React from "react";
import { SvgIcon } from "@material-ui/core";
import ShopingCart from "./shopping-cart.svg";

export default function ShoppingCartIcon(props) {
  return (
    <SvgIcon viewBox={"0 0 512 512"} fontSize={"large"} {...props}>
      {<ShopingCart />}
    </SvgIcon>
  );
}
