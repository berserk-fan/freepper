import { Fabric } from "@mamat14/shop-server/shop_model";

export type VicFabricKey =
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

const fabrics: Record<VicFabricKey, Fabric & { id: VicFabricKey }> = {
  "vic-20": {
    id: "vic-20",
    displayName: "Молочный",
    description: "",
    image: {
      src: "/fabrics/vic/vic-20.JPG",
      alt: "Молочная ткань",
    },
  },
  "vic-21": {
    id: "vic-21",
    displayName: "Капучино",
    description: "",
    image: {
      src: "/fabrics/vic/vic-21.JPG",
      alt: "Ткань цвета капучино",
    },
  },
  "vic-22": {
    id: "vic-22",
    displayName: "Olive Grey",
    description: "",
    image: {
      src: "/fabrics/vic/vic-22.JPG",
      alt: "Ткань оливково серого цвета",
    },
  },
  "vic-32": {
    id: "vic-32",
    displayName: "Хиллари",
    description: "",
    image: {
      src: "/fabrics/vic/vic-32.JPG",
      alt: "Ткань цвета хиллари",
    },
  },
  "vic-34": {
    id: "vic-34",
    displayName: "Кофе",
    description: "",
    image: {
      src: "/fabrics/vic/vic-34.JPG",
      alt: "Ткань цвета кофе",
    },
  },
  "vic-36": {
    id: "vic-36",
    displayName: "Шоколад",
    description: "",
    image: {
      src: "/fabrics/vic/vic-36.JPG",
      alt: "Ткань цвета шоколад",
    },
  },
  "vic-66": {
    id: "vic-66",
    displayName: "Орхидея",
    description: "",
    image: {
      src: "/fabrics/vic/vic-66.JPG",
      alt: "Ткань цвета орхидея",
    },
  },
  "vic-70": {
    id: "vic-70",
    displayName: "Васаби",
    description: "",
    image: {
      src: "/fabrics/vic/vic-70.JPG",
      alt: "Ткань цвета васаби",
    },
  },
  "vic-80": {
    id: "vic-80",
    displayName: "Аквамарин",
    description: "",
    image: {
      src: "/fabrics/vic/vic-80.JPG",
      alt: "Ткань цвета аквамарин",
    },
  },
  "vic-88": {
    id: "vic-88",
    displayName: "Синий зодиак",
    description: "",
    image: {
      src: "/fabrics/vic/vic-88.JPG",
      alt: "Ткань цвета синий зодиак",
    },
  },
  "vic-93": {
    id: "vic-93",
    displayName: "Серебро",
    description: "",
    image: {
      src: "/fabrics/vic/vic-93.JPG",
      alt: "Ткань цвета серебро",
    },
  },
  "vic-100": {
    id: "vic-100",
    displayName: "Черный",
    description: "",
    image: {
      src: "/fabrics/vic/vic-100.JPG",
      alt: "Ткань цвета черный",
    },
  },
};

export default Object.values(fabrics);
