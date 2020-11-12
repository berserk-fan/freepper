import Typography from "@material-ui/core/Typography";
import React from "react";
import Link from "next/link";

export default function HeaderLogo() {
  return (
    <Typography variant="h5" noWrap>
      <Link href={"/"}>Погладить можно?</Link>
    </Typography>
  );
}
