import React from "react";
import { Box, Toolbar } from "@material-ui/core";
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
