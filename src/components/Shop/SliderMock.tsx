import React from "react";
import { Box } from "@material-ui/core";
import { SliderProps } from "./Slider";

export default function SliderMock(props: SliderProps) {
  return <Box className={props.className}>{props.slides[0]}</Box>;
}
