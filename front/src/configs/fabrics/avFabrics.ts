import { Fabric } from "apis/catalog";

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
  | "av-18"
  | "av-06-10"
  | "av-07-18";

export const AV_FABRICS: Record<AvFabricKeys, Fabric & { id: AvFabricKeys }> = {
  "av-01": {
    id: "av-01",
    displayName: "Алюминий",
    description: "",
    imageSrc: "/fabrics/av/av-01.png",
    imageAlt: "Ткань цвета алюминий",
  },
  "av-02": {
    id: "av-02",
    displayName: "Серый туман",
    description: "",
    imageSrc: "/fabrics/av/av-02.png",
    imageAlt: "Ткань цвета серый туман",
  },
  "av-04": {
    id: "av-04",
    displayName: "Сантал",
    description: "",
    imageSrc: "/fabrics/av/av-04.png",
    imageAlt: "Ткань цвета сантал",
  },
  "av-06": {
    id: "av-06",
    displayName: "Какао",
    description: "",
    imageSrc: "/fabrics/av/av-06.png",
    imageAlt: "Ткань цвета какао",
  },
  "av-07": {
    id: "av-07",
    displayName: "Желтый",
    description: "",
    imageSrc: "/fabrics/av/av-07.png",
    imageAlt: "Ткань желтого цвета",
  },
  "av-10": {
    id: "av-10",
    displayName: "Пыльная роза",
    description: "",
    imageSrc: "/fabrics/av/av-10.png",
    imageAlt: "Ткань цвета пыльная роза",
  },
  "av-11": {
    id: "av-11",
    displayName: "Бирюза",
    description: "",
    imageSrc: "/fabrics/av/av-11.png",
    imageAlt: "Ткань цвета Бирюза",
  },
  "av-12": {
    id: "av-12",
    displayName: "Изумруд",
    description: "",
    imageSrc: "/fabrics/av/av-12.png",
    imageAlt: "Ткань цвета изумруд",
  },
  "av-13": {
    id: "av-13",
    displayName: "Гольфстрим",
    description: "",
    imageSrc: "/fabrics/av/av-13.png",
    imageAlt: "Ткань цвета васаби",
  },
  "av-14": {
    id: "av-14",
    displayName: "Астронавт",
    description: "",
    imageSrc: "/fabrics/av/av-14.png",
    imageAlt: "Ткань цвета астронавт",
  },
  "av-15": {
    id: "av-15",
    displayName: "Серебро",
    description: "",
    imageSrc: "/fabrics/av/av-15.png",
    imageAlt: "Ткань цвета серебро",
  },
  "av-17": {
    id: "av-17",
    displayName: "Серый",
    description: "",
    imageSrc: "/fabrics/av/av-17.png",
    imageAlt: "Ткань серого цвета",
  },
  "av-18": {
    id: "av-18",
    displayName: "Черный",
    description: "",
    imageSrc: "/fabrics/av/av-18.png",
    imageAlt: "Черная ткань",
  },
  "av-06-10": {
    id: "av-06-10",
    displayName: "Какао и Роза",
    description: "TODO",
    imageSrc: "/fabrics/av/av-06-10.png",
    imageAlt: "Ткань цвета какао и розы",
  },
  "av-07-18": {
    id: "av-07-18",
    displayName: "Желтый и Черный",
    description: "TODO",
    imageSrc: "/fabrics/av/av-07-18.png",
    imageAlt: "Ткань черного и розового цвета",
  },
};

export default Object.values(AV_FABRICS);
export const AV_KEYS = Object.keys(AV_FABRICS) as AvFabricKeys[];
