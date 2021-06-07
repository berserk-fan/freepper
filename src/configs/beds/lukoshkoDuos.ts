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
    src: `${folder}/IMG_4036.jpg`,
    alt: "Лукошко Дуо крупным планом",
    name: makeProductName(lukoshkoName, "vic-66", defaultSize),
  },
  IMG_4037: {
    src: `${folder}/IMG_4037.jpg`,
    alt: "Лукошко Дуо цвета орхидея в которой сидит черный кот",
    name: makeProductName(lukoshkoName, "vic-66", defaultSize),
  },
  IMG_4116: {
    src: `${folder}/IMG_4116.jpg`,
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
  "lukoshko-xs": { price: 1300 },
  "lukoshko-s": { price: 1400 },
  "lukoshko-m": { price: 1550 },
  "lukoshko-l": { price: 1700 },
};

const lukoshkoDescription = `
Тканина, фурнітура, наповнювачі:
 - Тканина прошита армованими нитками
 - Борти і матрац наповнені гіпоалергенним і антибактеріальним холлофайбером
 - Водовідштовхувальна тканина

«Лукошко» пошито з тканини зі спеціальним водовідштовхувальним просоченням: волога скачується і легко струшується з цього матеріалу. ⠀ 
Ми любимо її за оксамитову обробку матеріалу і повітряне наповнення. 
Матеріал має таке щільне переплетення, що навіть гостру, як ялинкові голочки шерсть, можна струсити одним рухом. Це ж властивість робить тканину дуже міцною і зносостійкого. Лежаночка має знімний чохол на подушечку, який можна прати в машинці.
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
