import React from "react";
import { Box } from "@material-ui/core";

export default function HideOnMobile({ display = "block", children }) {
  return <Box display={{ xs: "none", sm: display }}>{children}</Box>;
}
