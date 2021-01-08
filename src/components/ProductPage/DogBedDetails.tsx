import { DogBed } from "@mamat14/shop-server/shop_model";
import FabricPicker from "./FabricPicker";
import React from "react";

export default function ({
  details: { fabricId: curFabric, sizeId: curSize, variants, fabrics },
}: {
  details: DogBed;
}) {
  const hrefMap = new Map<string, string>();
  variants.forEach((v) => hrefMap.set(`${v.fabricId}-${v.sizeId}`, v.variantName));
  const fabricsWithRefs = fabrics.map(f => ({...f, ...{href: hrefMap.get(`${f.id}-${curSize}`)}}));
  return (
    <div>
      <FabricPicker cur={curFabric} fabrics={fabricsWithRefs}/>
    </div>
  );
}
