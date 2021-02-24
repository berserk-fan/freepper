import React from "react";
import { Box } from "@material-ui/core";

export default function SliderMock(props) {
  return <Box className={props.className}>{props.slides[0]}</Box>;
}
