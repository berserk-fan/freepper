import React, { memo } from "react";
import Link from "next/link";
import Box from "@material-ui/core/Box/Box";
import Logo from "../Logo/Logo";

function HeaderLogo({ className = "" }: { className?: string }) {
  return (
    <Box className={className}>
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
    </Box>
  );
}

export default memo(HeaderLogo);