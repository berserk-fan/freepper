import { Fabric, Size } from "@mamat14/shop-server/shop_model";
import React from "react";
import Image from "next/image";
import Picker from "./Picker";
import { Typography } from "@material-ui/core";

export default function SizePicker({
  selected,
  sizes,
}: {
  selected: string;
  sizes: (Size & { href: string })[];
}) {
  return (
    <div>
      <Typography gutterBottom variant={"subtitle2"} component={"h3"}>
        Размер
      </Typography>
      <Picker
        selectedId={selected}
        items={sizes.sort((a, b) => a.id.localeCompare(b.id))}
      />
    </div>
  );
}
