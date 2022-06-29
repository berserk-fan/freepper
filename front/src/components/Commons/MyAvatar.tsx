import Image from "next/image";
import React from "react";
import Avatar from "@mui/material/Avatar";

export function MyAvatar({
  image,
  variant = "small",
}: {
  image: { src: string; alt: string };
  variant?: "small" | "big" | "huge";
}) {
  let dim;
  if (variant === "small") {
    dim = 25;
  } else if (variant === "big") {
    dim = 50;
  } else if (variant === "huge") {
    dim = 75;
  } else {
    dim = 25;
  }
  return (
    <Avatar style={{ width: dim, height: dim }}>
      <Image width={dim} height={dim} src={image.src} alt={image.alt} />
    </Avatar>
  );
}
