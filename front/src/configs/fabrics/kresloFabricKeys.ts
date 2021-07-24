import { Fabric } from "apis/catalog";

export type KresloFabricKeys = "kr-black";

export const KRESLO_FABRICS: Record<
  KresloFabricKeys,
  Fabric & { id: KresloFabricKeys }
> = {
  "kr-black": {
    id: "kr-black",
    displayName: "Черный",
    description: "",
    imageSrc: "/fabrics/av/av-18.png",
    imageAlt: "Ткань черного цвета",
  },
};

export default Object.values(KRESLO_FABRICS);
export const KRESLO_KEYS = Object.keys(KRESLO_FABRICS) as KresloFabricKeys[];
