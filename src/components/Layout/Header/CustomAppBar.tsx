import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";

export const CustomAppBar = (props: AppBarProps) => {
  const { children } = props;
  return (
    <AppBar elevation={0} position="sticky" color={"inherit"} {...props}>
      {children}
    </AppBar>
  );
};
