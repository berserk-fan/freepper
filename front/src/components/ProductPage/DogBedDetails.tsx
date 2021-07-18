import { DogBed, Size } from "apis/catalog";
import React from "react";
import FabricPicker from "./FabricPicker";
import SizePicker from "./SizePicker";

const SEP = "-";
export default function DogBedDetails({
  details: { fabricId: curFabric, size: curSize, variants, fabrics, sizes },
  categoryName,
}: {
  categoryName: string;
  details: DogBed;
}) {
  const hrefMap = new Map<string, string>();
  variants.forEach((v) =>
    hrefMap.set(
      [v.fabricId, v.size].join(SEP),
      `/${categoryName}/${v.variantName}`,
    ),
  );
  const fabricsWithRefs = fabrics.map((f) => ({
    ...f,
    href: hrefMap.get([f.id, curSize].join(SEP)),
  }));
  const sizesWithRefs = Object.entries(sizes).map(([size, details]) => ({
    ...details,
    href: hrefMap.get([curFabric, size].join(SEP)),
    size: size as Size,
  }));
  const selectedFabric = fabrics.find((f) => f.id === curFabric);
  const selectedSizeDetails = sizes[curSize];
  return (
    <div>
      <FabricPicker selected={selectedFabric} fabrics={fabricsWithRefs} />
      <SizePicker
        selected={curSize}
        selectedDetails={selectedSizeDetails}
        sizes={sizesWithRefs}
      />
    </div>
  );
}
