import React from "react";
import Link from "next/link";
import Image from "next/image";
import { Box } from "@material-ui/core";

export default function HeaderLogo() {
  return (
    <Link href={"/"}>
      <Box
        width={200}
        height={100}
        className={"flex justify-center items-center overflow-hidden"}
      >
        <Box minWidth={256} minHeight={256}>
          <Image
            width={256}
            height={256}
            src={"/logo-512x512.png"}
            alt={"Лого сайта"}
            priority
          />
        </Box>
      </Box>
    </Link>
  );
}
