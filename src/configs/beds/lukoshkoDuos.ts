import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import lukoshkoSizes, { LukoshkoSizeKeys } from "configs/sizes/lukoshkoSizes";
import vicFabrics, { VicFabricKey } from "configs/fabrics/vicFabrics";
import { makeProductName } from "configs/beds/commons";

const lukoshkoName = "lukoshkoDuo";

function getLukoshkoVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of lukoshkoSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: makeProductName(lukoshkoName, fabric.id, size),
      });
    }
  }
  return res;
}

const lukoshkoVariants: DogBed_Variant[] = getLukoshkoVariants();
const defaultSize=lukoshkoSizes[0];
const folder = "/beds/lukoshko2";
export const lukoshkoDuoImages = {
  Dogs_7043: {
    src: `${folder}/Dogs-7043.jpg`,
    alt: "фото лежанки Лукошко",
    name: makeProductName(lukoshkoName, "vic-32", defaultSize),
  },
  IMG_4036: {
    src: `${folder}/IMG_4036.HEIC`,
    alt: "Лукошко Дуо крупным планом",
    name: makeProductName(lukoshkoName, "vic-66", defaultSize),
  },
  IMG_4037: {
    src: `${folder}/IMG_4037.HEIC`,
    alt: "Лукошко Дуо цвета орхидея в которой сидит черный кот",
    name: makeProductName(lukoshkoName, "vic-66", defaultSize),
  },
  IMG_4116: {
    src: `${folder}/IMG_4116.HEIC`,
    alt: "Лукошко Дуо цвета орхидея спереди с цветком",
    name: makeProductName(lukoshkoName, "vic-66", defaultSize),
  },
};

const images = lukoshkoDuoImages;

const imagesRaw: Record<VicFabricKey, ImageData[]> = {
  "vic-32": [images.Dogs_7043],
  "vic-20": [],
  "vic-21": [],
  "vic-22": [],
  "vic-34": [],
  "vic-36": [],
  "vic-66": [images.IMG_4036, images.IMG_4037, images.IMG_4116],
  "vic-70": [],
  "vic-80": [],
  "vic-88": [],
  "vic-93": [],
  "vic-100": [],
};

const lukoshkoImages: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries(imagesRaw).map(([id, photos]) => [
    id,
    photos.concat([{ src: `/fabrics/vic/${id}.JPG`, alt: "Фото ткани" }]),
  ]),
);

const lukoshkoPrices: Record<LukoshkoSizeKeys, Price> = {
  "lukoshko-xs": { price: 950 },
  "lukoshko-s": { price: 1050 },
  "lukoshko-m": { price: 1350 },
  "lukoshko-l": { price: 1550 },
};

const lukoshkoDescription = `
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

export const lukoshkoDuos: Product[] = lukoshkoVariants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Лукошко Дуо`,
  description: lukoshkoDescription,
  price: lukoshkoPrices[v.sizeId],
  images: lukoshkoImages[v.fabricId],
  modelName: "lukoshkoDuo",
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: vicFabrics,
      sizes: lukoshkoSizes,
      variants: lukoshkoVariants,
    },
  },
}));
