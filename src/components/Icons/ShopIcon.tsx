import React from "react";
import { SvgIcon } from "@material-ui/core";
import Shop from "./svg/shop.svg";
export default function ShopIcon(props) {
  return (
    <SvgIcon viewBox={"1 -8 511.999 511"} fontSize={"large"} {...props}>
      {<Shop />}
    </SvgIcon>
  );
}
