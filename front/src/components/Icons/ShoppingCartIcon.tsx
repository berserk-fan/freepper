import React, { memo } from "react";
import SvgIcon from "@mui/material/SvgIcon";
import ShopingCart from "./svg/shopping-cart.svg";

function ShoppingCartIcon(props) {
  return (
    <SvgIcon viewBox="0 0 512 512" fontSize="large" {...props}>
      <ShopingCart />
    </SvgIcon>
  );
}

export default memo(ShoppingCartIcon);
