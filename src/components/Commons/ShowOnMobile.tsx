import React from "react";
import { Box } from "@material-ui/core";

export default function ShowOnMobile({ display = "block", children }) {
  return <Box display={{ xs: "display", sm: "none" }}>{children}</Box>;
}
