import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar/Toolbar";
import React from "react";
import { CustomAppBar } from "./CustomAppBar";
import HeaderLogo from "./HeaderLogo";

export default function CheckoutHeader() {
  return (
    <CustomAppBar>
      <Toolbar>
        <Box className="mx-auto">
          <HeaderLogo />
        </Box>
      </Toolbar>
    </CustomAppBar>
  );
}
