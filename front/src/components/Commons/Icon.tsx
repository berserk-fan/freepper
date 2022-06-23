import Image from "next/image";
import React from "react";
import Avatar from "@mui/material/Avatar";

export function MyAvatar({
  image,
  variant = "small",
}: {
  image: { src: string; alt: string };
  variant?: "small" | "big";
}) {
  const dim = variant === "small" ? 24 : 56;
  return (
    <Avatar style={{ width: dim, height: dim }}>
      <Image width={dim} height={dim} src={image.src} alt={image.alt} />
    </Avatar>
  );
}
