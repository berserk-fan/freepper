import { DogBed } from "@mamat14/shop-server/shop_model";
import React from "react";
import FabricPicker from "./FabricPicker";
import SizePicker from "./SizePicker";

export default function DogBedDetails({
  details: { fabricId: curFabric, sizeId: curSize, variants, fabrics, sizes },
  categoryName,
}: {
  categoryName: string;
  details: DogBed;
}) {
  const hrefMap = new Map<string, string>();
  variants.forEach((v) =>
    hrefMap.set(`${v.fabricId}-${v.sizeId}`, `/${categoryName}/${v.variantName}`),
  );
  const fabricsWithRefs = fabrics.map((f) => ({
    ...f,
    ...{ href: hrefMap.get(`${f.id}-${curSize}`) },
  }));
  const sizesWithRefs = sizes.map((s) => ({
    ...s,
    ...{ href: hrefMap.get(`${curFabric}-${s.id}`) },
  }));
  const selectedFabric = fabrics.find(f => f.id === curFabric);
  const selectedSize = sizes.find(s => s.id === curSize);
  return (
    <div>
      <FabricPicker selected={selectedFabric} fabrics={fabricsWithRefs} />
      <SizePicker selected={selectedSize} sizes={sizesWithRefs} />
    </div>
  );
}
