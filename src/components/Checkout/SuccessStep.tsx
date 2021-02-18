import Button from "@material-ui/core/Button";
import Link from "next/link";
import {Typography, useTheme} from "@material-ui/core";
import React from "react";

export default function SuccessStep() {
  const theme = useTheme();
  return (
    <Button style={{ marginRight: theme.spacing(1) }}>
      <Link href={"/"}>
        <Typography>На главную</Typography>
      </Link>
    </Button>
  );
}
