import { Fabric } from "@mamat14/shop-server/shop_model";
import React from "react";
import Picker from "./Picker";
import Image from "next/image";
import { Avatar, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/styles";

function Icon({ item: fabric }: { item: Fabric }) {
  return (
    <Image
      width={24}
      height={24}
      src={fabric.image.src}
      alt={fabric.image.alt}
    />
  );
}

export default function FabricPicker({
  selected,
  fabrics,
}: {
  selected: string;
  fabrics: (Fabric & { href: string })[];
}) {
  return (
    <div>
      <Typography gutterBottom variant={"subtitle2"} component={"h3"}>
        Цвет
      </Typography>
      <Picker
        selectedId={selected}
        items={fabrics.sort((a, b) => a.id.localeCompare(b.id))}
        icon={Icon}
      />
    </div>
  );
}
