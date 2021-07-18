import { Size, SizeDetails } from "apis/catalog";
import Typography from "@material-ui/core/Typography/Typography";
import React from "react";
import Picker from "./Picker";

const weights: Record<Size, number> = {
  SIZE_UNSPECIFIED: 10000,
  XXXS: 1,
  XXS: 2,
  XS: 3,
  S: 4,
  M: 5,
  L: 6,
  XL: 7,
  XXL: 8,
  XXXL: 9,
};

export default function SizePicker({
  selected,
  selectedDetails,
  sizes,
}: {
  selected: Size;
  selectedDetails: SizeDetails;
  sizes: (SizeDetails & { href: string; size: Size })[];
}) {
  const items = sizes
    .sort((a, b) => weights[a.size] - weights[b.size])
    .map((x) => ({ id: x.size, displayName: x.size, href: x.href }));
  return (
    <div>
      <Typography gutterBottom variant="subtitle2" component="h3">
        Размер - ${selectedDetails.description}
      </Typography>
      <Picker selectedId={selected} items={items} />
    </div>
  );
}
