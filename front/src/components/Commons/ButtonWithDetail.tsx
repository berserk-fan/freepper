import Button, { ButtonProps } from "@mui/material/Button";
import ButtonGroup from "@mui/material/ButtonGroup";
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
      <Detail title={detailText} />
    </ButtonGroup>
  );
}
