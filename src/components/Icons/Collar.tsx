import SvgIcon from "@material-ui/core/SvgIcon";
import React, { memo } from "react";
import CollarIcon from "./svg/collar.svg";

function Collar(props) {
  return (
    <SvgIcon fontSize="large" viewBox="0 0 66 66" {...props}>
      <CollarIcon />
    </SvgIcon>
  );
}

export default memo(Collar);
