import React from "react";
import Link from "next/link";
import { Box } from "@material-ui/core";
import Logo from "../Logo/Logo";

export default function HeaderLogo() {
  return (
    <Link href="/">
      <Box
        width={200}
        height={100}
        className="flex justify-center items-center overflow-hidden"
        fontSize={240}
      >
        <Logo />
      </Box>
    </Link>
  );
}
