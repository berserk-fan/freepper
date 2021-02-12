import { Button, ButtonGroup, ButtonProps } from "@material-ui/core";
import React from "react";
import Detail from "./Detail";

export default function ButtonWithDetail({
  detailText,
  children,
  ...otherProps
}: { detailText: string } & ButtonProps & any): JSX.Element {
  return (
    <ButtonGroup>
      <Button {...otherProps}>{children}</Button>
      <Detail text={detailText} />
    </ButtonGroup>
  );
}
