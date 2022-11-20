import React, { memo } from "react";
import SvgIcon from "@mui/material/SvgIcon";
import House from "./svg/house.svg";

function HouseIcon(props) {
  return (
    <SvgIcon fontSize="large" viewBox="0 0 512 512" {...props}>
      <House />
    </SvgIcon>
  );
}
export default memo(HouseIcon);
