import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";
import { Slide, useScrollTrigger } from "@material-ui/core";

function HideOnScroll(props) {
  // Note that you normally won't need to set the window ref as useScrollTrigger
  // will default to window.
  // This is only being set here because the demo is in an iframe.
  const trigger = useScrollTrigger();

  return (
    <Slide appear={false} direction="down" in={props.show || !trigger}>
      {props.children}
    </Slide>
  );
}

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
