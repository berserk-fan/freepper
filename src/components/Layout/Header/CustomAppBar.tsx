import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";

export const CustomAppBar = (props: AppBarProps) => {
  const { children } = props;
  return (
    <AppBar color="inherit" position="sticky" {...props}>
      {children}
    </AppBar>
  );
};
