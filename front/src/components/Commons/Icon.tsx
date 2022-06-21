import Image from "next/image";
import React from "react";
import Avatar from "@material-ui/core/Avatar/Avatar";

export function MyAvatar({
  image,
  variant = "small",
}: {
  image: { src: string; alt: string };
  variant?: "small" | "big";
}) {
  const dim = variant === "small" ? 24 : 56;
  return (
    <Avatar style={{ width: 56, height: 56 }}>
      <Image width={dim} height={dim} src={image.src} alt={image.alt} />
    </Avatar>
  );
}
