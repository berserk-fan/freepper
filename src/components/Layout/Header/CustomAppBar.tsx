import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";
import dynamic from "next/dynamic";
import {Box} from "@material-ui/core";
const HideOnScroll = dynamic(() => import("./HideOnScroll"), {loading: (children) => <Box>{children}</Box>});

export const CustomAppBar = (props: AppBarProps & { show?: boolean }) => {
  const { children } = props;
  return (
    <HideOnScroll {...props}>
      <AppBar elevation={0} position={"sticky"} color={"inherit"} {...props}>
        {children}
      </AppBar>
    </HideOnScroll>
  );
};
