import Slide from "@material-ui/core/Slide/Slide";
import useScrollTrigger from "@material-ui/core/useScrollTrigger/useScrollTrigger";
import React from "react";

export default function HideOnScroll(props) {
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
