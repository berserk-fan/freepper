import { Grid } from "@material-ui/core";
import React, { Children } from "react";

export default function Spacing(props) {
  const {
    spacing,
    children,
    className = "",
    childClassName = "",
    ...otherProps
  } = props;
  return (
    <Grid container className={className} spacing={spacing} {...otherProps}>
      {Children.map(children, (c) => (
        <Grid item className={childClassName}>
          {c}
        </Grid>
      ))}
    </Grid>
  );
}
