import Box from "@mui/material/Box";
import React from "react";

export default function ShowOnMobile({ children }) {
  return <Box display={{ xs: "display", sm: "none" }}>{children}</Box>;
}
