import React from "react";
import { SvgIcon } from "@material-ui/core";
import House from "./house.svg";
export default function HouseIcon(props) {
  return (
    <SvgIcon fontSize={"large"} viewBox={"0 0 512 512"} {...props}>
      {<House />}
    </SvgIcon>
  );
}
