import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import vicFabrics, { VicFabricKey } from "configs/fabrics/vicFabrics";
import kvadroSizes, { KvadroSizeKeys } from "configs/sizes/kvadroSizes";
import {makeProductName} from "./commons";
import chemodanSizes from "../sizes/chemodanSizes";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of kvadroSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: makeProductName("kvadroSoft", fabric.id, size),
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();

const defaultSize = chemodanSizes[0];
export const kvadroImages = {
  kvadroWithPug: {
    src: "/beds/kvadro-soft/Dogs-7152 (1).jpg",
    alt: "Лежанка Квадро изумрудного цвета на которой сидит мопс",
    name: makeProductName("kvadroSoft", "vic-70", defaultSize),
  },
  kvadroEmerald: {
    src: "/beds/kvadro-soft/Dogs-7234.jpg",
    alt: "Лежанка Квадро изумрудного цвета",
    name:makeProductName("kvadroSoft", "vic-70", defaultSize),
  },
  kvadroEmerald2: {
    src: "/beds/kvadro-soft/Dogs-7239.jpg",
    alt: "Лежанка Квадро изумрудного цвета",
    name: makeProductName("kvadroSoft", "vic-70", defaultSize),
  },
  kvadroEmerald3: {
    src: "/beds/kvadro-soft/Dogs-7278.jpg",
    alt: "Лежанка Квадро изумрудного цвета",
    name: makeProductName("kvadroSoft", "vic-70", defaultSize),
  },
};

const images = kvadroImages;
const fabricToImage: Record<VicFabricKey, ImageData[]> = {
  "vic-20": [],
  "vic-21": [],
  "vic-22": [],
  "vic-32": [],
  "vic-34": [],
  "vic-36": [],
  "vic-66": [],
  "vic-70": [
    images.kvadroWithPug,
    images.kvadroEmerald,
    images.kvadroEmerald2,
    images.kvadroEmerald3,
  ],
  "vic-80": [],
  "vic-88": [],
  "vic-93": [],
  "vic-100": [],
};

const imagesWithFabrics: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries(fabricToImage).map(([id, photos]: [string, ImageData[]]) => [
    id,
    photos.concat([
      {
        src: `/fabrics/vic/${id}.JPG`,
        alt: "Фото ткани из которой сделана лежанка Квадро Стронг",
      },
    ]),
  ]),
);

const prices: Record<KvadroSizeKeys, Price> = {
  "kvadro-xs": { price: 950 },
  "kvadro-s": { price: 1050 },
  "kvadro-m": { price: 1350 },
  "kvadro-l": { price: 1550 },
  "kvadro-xl": { price: 1750 },
  "kvadro-xxl": { price: 2000 },
};

const description = `
Тканина, фурнітура, наповнювачі:
 - Тканина прошита армованими нитками
 - Борти і матрац наповнені гіпоалергенним і антибактеріальним холлофайбером
 - Водовідштовхувальна тканина

Характеристики: 
 - Акуратні, м'які бортики, на які улюбленець може покласти голову 
 - Лежак НЕ боїться води 
 - Чохол, який можна прати в пральній машині
 - З’йомна подушка
`;

const kvadroSofts: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Квадро Софт`,
  description,
  price: prices[v.sizeId],
  images: imagesWithFabrics[v.fabricId],
  modelName: "kvadroSoft",
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: vicFabrics,
      sizes: kvadroSizes,
      variants,
    },
  },
}));

export default kvadroSofts;
