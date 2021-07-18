import { Fabric } from "apis/catalog";

export type VicFabricKeys =
  | "vic-20"
  | "vic-21"
  | "vic-22"
  | "vic-32"
  | "vic-34"
  | "vic-36"
  | "vic-66"
  | "vic-70"
  | "vic-80"
  | "vic-88"
  | "vic-93"
  | "vic-100";

export const VIC_FABRICS: Record<
  VicFabricKeys,
  Fabric & { id: VicFabricKeys }
> = {
  "vic-20": {
    id: "vic-20",
    displayName: "Молочный",
    description: "",
    imageSrc: "/fabrics/vic/vic-20.JPG",
    imageAlt: "Молочная ткань",
  },
  "vic-21": {
    id: "vic-21",
    displayName: "Капучино",
    description: "",
    imageSrc: "/fabrics/vic/vic-21.JPG",
    imageAlt: "Ткань цвета капучино",
  },
  "vic-22": {
    id: "vic-22",
    displayName: "Olive Grey",
    description: "",
    imageSrc: "/fabrics/vic/vic-22.JPG",
    imageAlt: "Ткань оливково серого цвета",
  },

  "vic-32": {
    id: "vic-32",
    displayName: "Хиллари",
    description: "",
    imageSrc: "/fabrics/vic/vic-32.JPG",
    imageAlt: "Ткань цвета хиллари",
  },

  "vic-34": {
    id: "vic-34",
    displayName: "Кофе",
    description: "",
    imageSrc: "/fabrics/vic/vic-34.JPG",
    imageAlt: "Ткань цвета кофе",
  },
  "vic-36": {
    id: "vic-36",
    displayName: "Шоколад",
    description: "",

    imageSrc: "/fabrics/vic/vic-36.JPG",
    imageAlt: "Ткань цвета шоколад",
  },
  "vic-66": {
    id: "vic-66",
    displayName: "Орхидея",
    description: "",
    imageSrc: "/fabrics/vic/vic-66.JPG",
    imageAlt: "Ткань цвета орхидея",
  },

  "vic-70": {
    id: "vic-70",
    displayName: "Васаби",
    description: "",
    imageSrc: "/fabrics/vic/vic-70.JPG",
    imageAlt: "Ткань цвета васаби",
  },

  "vic-80": {
    id: "vic-80",
    displayName: "Аквамарин",
    description: "",
    imageSrc: "/fabrics/vic/vic-80.JPG",
    imageAlt: "Ткань цвета аквамарин",
  },

  "vic-88": {
    id: "vic-88",
    displayName: "Синий зодиак",
    description: "",
    imageSrc: "/fabrics/vic/vic-88.JPG",
    imageAlt: "Ткань цвета синий зодиак",
  },

  "vic-93": {
    id: "vic-93",
    displayName: "Серебро",
    description: "",
    imageSrc: "/fabrics/vic/vic-93.JPG",
    imageAlt: "Ткань цвета серебро",
  },

  "vic-100": {
    id: "vic-100",
    displayName: "Черный",
    description: "",
    imageSrc: "/fabrics/vic/vic-100.JPG",
    imageAlt: "Ткань цвета черный",
  },
};

export default Object.values(VIC_FABRICS);
export const VIC_KEYS = Object.keys(VIC_FABRICS) as VicFabricKeys[];
