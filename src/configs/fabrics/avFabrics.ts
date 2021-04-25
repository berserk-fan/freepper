import { Fabric } from "@mamat14/shop-server/shop_model";

export type AvFabricKeys =
  | "av-01"
  | "av-02"
  | "av-04"
  | "av-06"
  | "av-07"
  | "av-10"
  | "av-11"
  | "av-12"
  | "av-13"
  | "av-14"
  | "av-15"
  | "av-17"
  | "av-18";

const avFabrics: Record<string, Fabric & { id: AvFabricKeys }> = {
  "av-01": {
    id: "av-01",
    displayName: "Алюминий",
    description: "",
    image: {
      src: "/fabrics/av/av-01.png",
      alt: "Ткань цвета алюминий",
    },
  },
  "av-02": {
    id: "av-02",
    displayName: "Серый туман",
    description: "",
    image: {
      src: "/fabrics/av/av-02.png",
      alt: "Ткань цвета серый туман",
    },
  },
  "av-04": {
    id: "av-04",
    displayName: "Сантал",
    description: "",
    image: {
      src: "/fabrics/av/av-04.png",
      alt: "Ткань цвета сантал",
    },
  },
  "av-06": {
    id: "av-06",
    displayName: "Какао",
    description: "",
    image: {
      src: "/fabrics/av/av-06.png",
      alt: "Ткань цвета какао",
    },
  },
  "av-07": {
    id: "av-07",
    displayName: "Желтый",
    description: "",
    image: {
      src: "/fabrics/av/av-07.png",
      alt: "Ткань желтого цвета",
    },
  },
  "av-10": {
    id: "av-10",
    displayName: "Пыльная роза",
    description: "",
    image: {
      src: "/fabrics/av/av-10.png",
      alt: "Ткань цвета пыльная роза",
    },
  },
  "av-11": {
    id: "av-11",
    displayName: "Бирюза",
    description: "",
    image: {
      src: "/fabrics/av/av-11.png",
      alt: "Ткань цвета Бирюза",
    },
  },
  "av-12": {
    id: "av-12",
    displayName: "Изумруд",
    description: "",
    image: {
      src: "/fabrics/av/av-12.png",
      alt: "Ткань цвета изумруд",
    },
  },
  "av-13": {
    id: "av-13",
    displayName: "Гольфстрим",
    description: "",
    image: {
      src: "/fabrics/av/av-13.png",
      alt: "Ткань цвета васаби",
    },
  },
  "av-14": {
    id: "av-14",
    displayName: "Астронавт",
    description: "",
    image: {
      src: "/fabrics/av/av-14.png",
      alt: "Ткань цвета астронавт",
    },
  },
  "av-15": {
    id: "av-15",
    displayName: "Серебро",
    description: "",
    image: {
      src: "/fabrics/av/av-15.png",
      alt: "Ткань цвета серебро",
    },
  },
  "av-17": {
    id: "av-17",
    displayName: "Серый",
    description: "",
    image: {
      src: "/fabrics/av/av-17.png",
      alt: "Ткань серого цвета",
    },
  },
  "av-18": {
    id: "av-18",
    displayName: "Черный",
    description: "",
    image: {
      src: "/fabrics/av/av-18.png",
      alt: "Черная ткань",
    },
  },
};
export default Object.values(avFabrics);
