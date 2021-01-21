import AppBar from "@material-ui/core/AppBar";
import React from "react";
import { AppBarProps } from "@material-ui/core/AppBar/AppBar";
import { useScrollTrigger } from "@material-ui/core";

function ElevationScroll(props) {
  const { children, window } = props;
  // Note that you normally won't need to set the window ref as useScrollTrigger
  // will default to window.
  // This is only being set here because the demo is in an iframe.
  const trigger = useScrollTrigger({
    disableHysteresis: true,
    threshold: 0,
    target: window ? window() : undefined,
  });

  return React.cloneElement(children, {
    elevation: trigger ? 4 : 0,
  });
}

export const CustomAppBar = (props: AppBarProps) => {
  const { children } = props;
  return (
    <ElevationScroll {...props}>
      <AppBar position="sticky" color={"primary"} {...props}>
        {children}
      </AppBar>
    </ElevationScroll>
  );
};
