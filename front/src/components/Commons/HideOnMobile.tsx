import Box from "@mui/material/Box/Box";
import React from "react";

export default function HideOnMobile({ display = "block", children }) {
  return <Box display={{ xs: "none", sm: display }}>{children}</Box>;
}
