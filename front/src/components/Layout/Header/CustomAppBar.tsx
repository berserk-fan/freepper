import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";
import HideOnScroll from "./HideOnScroll";

export const CustomAppBar = (props: AppBarProps & { show?: boolean }) => {
  const { children } = props;
  return (
    <HideOnScroll {...props}>
      <AppBar elevation={0} position="sticky" color="inherit" {...props}>
        {children}
      </AppBar>
    </HideOnScroll>
  );
};
