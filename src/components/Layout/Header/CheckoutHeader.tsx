import Box from "@material-ui/core/Box/Box";
import Toolbar from "@material-ui/core/Toolbar/Toolbar";
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
