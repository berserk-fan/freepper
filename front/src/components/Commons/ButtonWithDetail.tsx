import Button, { ButtonProps } from "@mui/material/Button/Button";
import ButtonGroup from "@mui/material/ButtonGroup/ButtonGroup";
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
