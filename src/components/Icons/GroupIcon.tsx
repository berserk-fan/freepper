import React from "react";
import { SvgIcon } from "@material-ui/core";
import Group from "./svg/group.svg";
export default function GroupIcon(props) {
  return (
    <SvgIcon fontSize={"large"} viewBox={"0 0 512 512"} {...props}>
      {<Group />}
    </SvgIcon>
  );
}
