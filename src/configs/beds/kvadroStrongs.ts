import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import avFabrics, { AvFabricKeys } from "configs/fabrics/avFabrics";
import kvadroSizes, { KvadroSizeKeys } from "configs/sizes/kvadroSizes";
import {makeProductName} from "./commons";
import chemodanSizes from "../sizes/chemodanSizes";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of avFabrics) {
    for (const size of kvadroSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: makeProductName("kvadroStrong", fabric.id, size),
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();

const defaultSize = chemodanSizes[0];
export const kvadroStrongImages = {
  kvadroStrongWithDog: {
    src: "/beds/kvadro-strong/Dogs-24890.jpg",
    alt: "Собака прямо в Квадро стронг",
    name: makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongFull: {
    src: "/beds/kvadro-strong/Dogs-7248.jpg",
    alt: " Квадро стронг полностью",
    name: makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongLabel: {
    src: "/beds/kvadro-strong/Dogs-7249.jpg",
    alt: "Этикетка в Квадро стронг",
    name: makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongPillow: {
    src: "/beds/kvadro-strong/Dogs-7251.jpg",
    alt: "Подушка в Квадро стронг",
    name: makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongFabric: {
    src: "/beds/kvadro-strong/Dogs-7326.jpg",
    alt: "Фото ткани в Квадро стронг",
    name:makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongFabric2: {
    src: "/beds/kvadro-strong/Dogs-7332.jpg",
    alt: "Фото ткани в Квадро стронг",
    name: makeProductName("kvadroStrong", "av-11", defaultSize),
  },
  kvadroStrongDogFromAbove: {
    src: "/beds/kvadro-strong/Dogs-24875.jpg",
    alt: "Собака сверху в Квадро стронг",
    name: makeProductName("kvadroStrong", "av-11", defaultSize)
  }
};
const images = kvadroStrongImages;

const fabricToImages: Record<AvFabricKeys, ImageData[]> = {
  "av-01": [],
  "av-02": [],
  "av-04": [],
  "av-06": [],
  "av-07": [],
  "av-10": [],
  "av-11": [
    images.kvadroStrongWithDog,
    images.kvadroStrongFull,
    images.kvadroStrongLabel,
    images.kvadroStrongPillow,
    images.kvadroStrongFabric,
    images.kvadroStrongDogFromAbove,
    images.kvadroStrongFabric2,
  ],
  "av-12": [],
  "av-13": [],
  "av-14": [],
  "av-15": [],
  "av-17": [],
  "av-18": [],
};

const imagesWithFabric: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries(fabricToImages).map(([id, photos]: [string, ImageData[]]) => [
    id,
    photos.concat([
      { src: `/fabrics/av/${id}.png`, alt: "Фото ткани лежанки квадро стронг" },
    ]),
  ]),
);

const prices: Record<KvadroSizeKeys, Price> = {
  "kvadro-xs": { price: 1050 },
  "kvadro-s": { price: 1200 },
  "kvadro-m": { price: 1450 },
  "kvadro-l": { price: 1600 },
};

const description = `
# О лежанке
Наша лежаночка состоит из водоотталкивающей ткани
* подушечки и бортики со съемным чехлом
* наполнителя: холофайбер.

# О размерах
* XS (50 х 40 см) для мини йорков, той-терьеров, чихуахуа и др. малышек
* S (60 х 40 см) для шпицев, папильонов, мальтийских болонок
* M (70 х 50 см) для ши-тцу, французских бульдогов, цверг-шнауцеров, вест хайленд уайт терьеров, пекинесов, мейн-кунов, британских вислоухих.
* L (90 х 60 см) для корги, биглей, американских бультерьеров, английских бульдогов, русских кокер спаниелей.
`;

const kvadroStrongs: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Чемодан`,
  description,
  price: prices[v.sizeId],
  images: imagesWithFabric[v.fabricId],
  modelName: "kvadroStrong",
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: avFabrics,
      sizes: kvadroSizes,
      variants,
    },
  },
}));

export default kvadroStrongs;
