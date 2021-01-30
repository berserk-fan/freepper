import { DogBed } from "@mamat14/shop-server/shop_model";
import FabricPicker from "./FabricPicker";
import React from "react";
import SizePicker from "./SizePicker";

export default function DogBedDetails({
  details: { fabricId: curFabric, sizeId: curSize, variants, fabrics, sizes },
  categoryName
}: {
  categoryName: string;
  details: DogBed;
}) {
  const hrefMap = new Map<string, string>();
  variants.forEach((v) =>
    hrefMap.set(`${v.fabricId}-${v.sizeId}`, `/${categoryName}/${v.variantName}`)
  );
  const fabricsWithRefs = fabrics.map((f) => ({
    ...f,
    ...{ href: hrefMap.get(`${f.id}-${curSize}`) },
  }));
  const sizesWithRefs = sizes.map((s) => ({
    ...s,
    ...{ href: hrefMap.get(`${curFabric}-${s.id}`) },
  }));
  return (
    <div>
      <FabricPicker selected={curFabric} fabrics={fabricsWithRefs} />
      <SizePicker selected={curSize} sizes={sizesWithRefs} />
    </div>
  );
}
