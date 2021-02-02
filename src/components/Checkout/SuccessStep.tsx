import Button from "@material-ui/core/Button";
import Link from "next/link";
import { Typography } from "@material-ui/core";
import React from "react";
import theme from "../../theme";

export default function SuccessStep() {
  return (
    <Button style={{ marginRight: theme.spacing(1) }}>
      <Link href={"/"}>
        <Typography>На главную</Typography>
      </Link>
    </Button>
  );
}
