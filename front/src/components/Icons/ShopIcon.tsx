import React, { memo } from "react";
import SvgIcon from "@mui/material/SvgIcon";
import Shop from "./svg/shop.svg";

function ShopIcon(props) {
  return (
    <SvgIcon viewBox="1 -8 511.999 511" fontSize="large" {...props}>
      <Shop />
    </SvgIcon>
  );
}

export default memo(ShopIcon);
