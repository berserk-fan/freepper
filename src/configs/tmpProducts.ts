import { Price, Product, ImageData } from "@mamat14/shop-server/shop_model";
import { lukoshkoDuoImages, lukoshkoDuos } from "configs/beds/lukoshkoDuos";
import lukoshkoTrios, { lukoshkoTrioImages } from "configs/beds/lukoshkoTrios";
import chemodans, { chemodanImages } from "configs/beds/chemodans";
import kvadroSofts, { kvadroImages } from "configs/beds/kvadroSofts";
import kvadroStrongs, { kvadroStrongImages } from "configs/beds/kvadroStrongs";

function min(arr: Product[]): Price {
  const min = arr.map((s) => s.price.price).reduce((a, b) => Math.min(a, b));
  return { price: min };
}

export type ImageWithName = ImageData & { name: string };
export type TmpGroupedProduct = Omit<Product, "images"> & {
  images: ImageWithName[];
};

export const tmpProducts: TmpGroupedProduct[] = [
  {
    id: "tmpChemodan",
    name: chemodans[0].name,
    displayName: "Чемодан",
    description: "",
    price: min(chemodans),
    images: Object.values(chemodanImages),
  },
  {
    id: "tmpLukoshko",
    name: lukoshkoDuos[0].name,
    displayName: "Лукошко Дуо",
    description: "",
    price: min(lukoshkoDuos),
    images: Object.values(lukoshkoDuoImages),
  },
  {
    id: "tmpLukoshkoEifel",
    name: lukoshkoTrios[0].name,
    displayName: "Лукошко Трио",
    description: "",
    price: min(lukoshkoTrios),
    images: Object.values(lukoshkoTrioImages),
  },
  {
    id: "tmpKvadro",
    name: kvadroSofts[0].name,
    displayName: "Квадро Soft",
    description: "",
    price: min(kvadroSofts),
    images: Object.values(kvadroImages),
  },
  {
    id: "tmpKvadroStrong",
    name: kvadroStrongs[0].name,
    displayName: "Квадро Стронг",
    description: "",
    price: min(kvadroStrongs),
    images: Object.values(kvadroStrongImages),
  },
];
