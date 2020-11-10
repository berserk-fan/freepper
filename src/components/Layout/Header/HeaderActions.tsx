import Link from "next/link";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import React from "react";

export default function (props) {
  return (
    <ButtonGroup {...props} color="primary" aria-label="page tabs">
      <Link href={"/"}>
        <Button>Домой</Button>
      </Link>
      <Link href={"/shop"}>
        <Button>Магазин</Button>
      </Link>
      <Link href={"/about"}>
        <Button>О нас</Button>
      </Link>
    </ButtonGroup>
  );
}
