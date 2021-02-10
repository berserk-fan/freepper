import React from "react";
import { SvgIcon } from "@material-ui/core";
import CollarIcon from "./svg/collar.svg";

export default function Collar(props) {
  return (
    <SvgIcon fontSize={"large"} viewBox={"0 0 66 66"} {...props}>
      <CollarIcon />
    </SvgIcon>
  );
}
