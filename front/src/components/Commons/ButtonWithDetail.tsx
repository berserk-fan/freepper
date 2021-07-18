import Button, { ButtonProps } from "@material-ui/core/Button/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup/ButtonGroup";
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
