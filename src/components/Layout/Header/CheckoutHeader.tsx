import { CustomAppBar } from "./CustomAppBar";
import React from "react";
import HeaderLogo from "./HeaderLogo";
import { Box, Toolbar } from "@material-ui/core";

export default function CheckoutHeader() {
  return (
    <CustomAppBar>
      <Toolbar>
        <Box className={"mx-auto"}>
          <HeaderLogo />
        </Box>
      </Toolbar>
    </CustomAppBar>
  );
}
