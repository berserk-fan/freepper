import React, { memo } from "react";
import SvgIcon from "@mui/material/SvgIcon";
import Group from "./svg/group.svg";

function GroupIcon(props) {
  return (
    <SvgIcon fontSize="large" viewBox="0 0 512 512" {...props}>
      <Group />
    </SvgIcon>
  );
}
export default memo(GroupIcon);
