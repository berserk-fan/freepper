import {
  Category,
  DogBed_Variant,
  Fabric,
  ImageData,
  Model,
  Price,
  Product,
  Size,
  SizeDetails,
} from "apis/catalog";

import { AV_FABRICS, AV_KEYS, AvFabricKeys } from "../fabrics/avFabrics";
import { VIC_FABRICS, VIC_KEYS, VicFabricKeys } from "../fabrics/vicFabrics";
import ALL_IMAGES from "./images.json";

export type BedKey =
  | "lukoshkoDuo"
  | "lukoshkoTrio"
  | "chemodan"
  | "kvadroSoft"
  | "kvadroStrong"
  | "norka"
  | "podushka"
  | "kolorado";

const helper1: Record<BedKey, 1> = {
  norka: 1,
  podushka: 1,
  chemodan: 1,
  kvadroSoft: 1,
  kvadroStrong: 1,
  lukoshkoDuo: 1,
  lukoshkoTrio: 1,
  kolorado: 1,
};
const productKeys = Object.keys(helper1);
export function isProductKey(t: string): t is BedKey {
  return !!productKeys.find((x) => x === t);
}

export type FabricKey = VicFabricKeys | AvFabricKeys;
type AvBeds = "chemodan" | "kolorado" | "kvadroStrong" | "norka";
export type FabricKey2<T> = T extends AvBeds ? AvFabricKeys : VicFabricKeys;
type Helper2 = {
  [P in BedKey]: FabricKey2<P>[];
};

export const MODEL_TO_FABRIC: Helper2 = {
  chemodan: AV_KEYS,
  kolorado: AV_KEYS,
  kvadroSoft: VIC_KEYS,
  kvadroStrong: AV_KEYS,
  lukoshkoDuo: VIC_KEYS,
  lukoshkoTrio: VIC_KEYS,
  norka: AV_KEYS,
  podushka: VIC_KEYS,
};

export function isFabricKeyOf<T extends BedKey>(
  t: T,
  fabricId: string,
): fabricId is FabricKey2<T> {
  // @ts-ignore
  return MODEL_TO_FABRIC[t].includes(fabricId);
}

export function makeProductName(name: BedKey, fabric: FabricKey, size: string) {
  return `products/${name}-${fabric}-${size}`;
}

type Prices = Partial<Record<Size, Price>>;
type AllPrices = Record<BedKey, Prices>;

type Sizes = Partial<Record<Size, SizeDetails>>;
type AllSizes = Record<BedKey, Sizes>;

type Fabrics<T extends BedKey> = Record<
  FabricKey2<T>,
  Fabric & { id: FabricKey2<T> }
>;
type AllFabrics = {
  [P in BedKey]: Fabrics<P>;
};
const ALL_PRICES: AllPrices = {
  kolorado: {
    S: { price: 1250 },
    M: { price: 1400 },
    L: { price: 1600 },
    XL: { price: 1800 },
  },
  norka: {
    XS: { price: 1550 },
    S: { price: 1650 },
    M: { price: 1800 },
    L: { price: 2100 },
  },
  podushka: {
    S: { price: 400 },
    M: { price: 500 },
    L: { price: 600 },
  },
  chemodan: {
    S: { price: 1350 },
    M: { price: 1500 },
    L: { price: 1650 },
    XL: { price: 1800 },
    XXL: { price: 2100 },
  },
  kvadroSoft: {
    XS: { price: 950 },
    S: { price: 1050 },
    M: { price: 1350 },
    L: { price: 1550 },
    XL: { price: 1750 },
    XXL: { price: 2000 },
  },
  kvadroStrong: {
    XS: { price: 1050 },
    S: { price: 1200 },
    M: { price: 1450 },
    L: { price: 1600 },
    XL: { price: 1850 },
    XXL: { price: 2150 },
  },
  lukoshkoDuo: {
    XS: { price: 1300 },
    S: { price: 1400 },
    M: { price: 1550 },
    L: { price: 1700 },
  },
  lukoshkoTrio: {
    XS: { price: 1350 },
    S: { price: 1450 },
    M: { price: 1600 },
    L: { price: 1750 },
  },
};

const KOLORADO_SIZES = {
  S: { description: "50x50см" },
  M: { description: "60x60см" },
  L: { description: "70x70см" },
  XL: { description: "80x80см" },
};

const NORKA_SIZES = {
  XS: { description: "50см" },
  S: { description: "60см" },
  M: { description: "70см" },
  L: { description: "80см" },
};

const PODUSHKA_SIZES = {
  S: { description: "45x35см" },
  M: { description: "60x40см" },
  L: { description: "65х45см" },
};

const CHEMODAN_SIZES: Sizes = {
  S: { description: "70x50см" },
  M: { description: "90x60см" },
  L: { description: "110x70см" },
  XL: { description: "120x80см" },
  XXL: { description: "140x100см" },
};

const KVADRO_SIZES: Sizes = {
  XS: { description: "50x40см" },
  S: { description: "60x45см" },
  M: { description: "70x50см" },
  L: { description: "90х60см" },
  XL: { description: "110x70см" },
  XXL: { description: "140x100см" },
};

const LUKOSHKO_SIZES: Sizes = {
  XS: { description: "Диаметр 50см" },
  S: { description: "Диаметр 60см" },
  M: { description: "Диаметр 70см" },
  L: { description: "Диаметр 80см" },
};

export const ALL_SIZES: AllSizes = {
  kolorado: KOLORADO_SIZES,
  norka: NORKA_SIZES,
  podushka: PODUSHKA_SIZES,
  chemodan: CHEMODAN_SIZES,
  kvadroSoft: KVADRO_SIZES,
  kvadroStrong: KVADRO_SIZES,
  lukoshkoDuo: LUKOSHKO_SIZES,
  lukoshkoTrio: LUKOSHKO_SIZES,
};

const ALL_FABRICS: AllFabrics = {
  kolorado: AV_FABRICS,
  norka: AV_FABRICS,
  podushka: VIC_FABRICS,
  chemodan: AV_FABRICS,
  kvadroSoft: VIC_FABRICS,
  kvadroStrong: AV_FABRICS,
  lukoshkoDuo: VIC_FABRICS,
  lukoshkoTrio: VIC_FABRICS,
};

const CHEMODAN_DESCRIPTION = `
Тканина, фурнітура, наповнювачі:
 - Тканина прошита армованими нитками
 - Матрац наповнен гіпоалергенним і антибактеріальним холлофайбером та синтепоном
 - Водовідштовхувальна тканина
`;

const KVADRO_DESCRIPTION = `
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

const LUKOSHKO_DESCRIPTION = `
Тканина, фурнітура, наповнювачі:
 - Тканина прошита армованими нитками
 - Борти і матрац наповнені гіпоалергенним і антибактеріальним холлофайбером
 - Водовідштовхувальна тканина

«Лукошко» пошито з тканини зі спеціальним водовідштовхувальним просоченням: волога скачується і легко струшується з цього матеріалу. ⠀ 
Ми любимо її за оксамитову обробку матеріалу і повітряне наповнення. 
Матеріал має таке щільне переплетення, що навіть гостру, як ялинкові голочки шерсть, можна струсити одним рухом. Це ж властивість робить тканину дуже міцною і зносостійкого. Лежаночка має знімний чохол на подушечку, який можна прати в машинці.
`;

const DESCRIPTIONS: Record<BedKey, string> = {
  chemodan: CHEMODAN_DESCRIPTION,
  kvadroSoft: KVADRO_DESCRIPTION,
  kvadroStrong: KVADRO_DESCRIPTION,
  lukoshkoDuo: LUKOSHKO_DESCRIPTION,
  lukoshkoTrio: LUKOSHKO_DESCRIPTION,
  kolorado: "TODO",
  norka: "TODO",
  podushka: "TODO",
};

export const PRODUCT_NAMES: Record<BedKey, string> = {
  chemodan: "Чемодан",
  kvadroSoft: "Квадро Софт",
  kvadroStrong: "Квадро Стронг",
  lukoshkoDuo: "Лукошко Дуо",
  lukoshkoTrio: "Лукошко Трио",
  kolorado: "Колорадо",
  norka: "Норка",
  podushka: "Подушка",
};

function makeProducts<T extends BedKey>(
  productKey: T,
  productName: T,
  prices: Prices,
  description: string,
  images: Record<string, ImageData[] | undefined>,
  sizes: Sizes,
  fabrics: Fabrics<T>,
) {
  const sizesIds = Object.keys(sizes) as Size[];
  const allFabrics: (Fabric & { id: FabricKey2<T> })[] = Object.values(fabrics);
  const fabricsSeq = allFabrics.filter(
    (fabric: Fabric & { id: FabricKey2<T> }) => !!images[fabric.id],
  );
  const variants: DogBed_Variant[] = [];
  for (const fabric of fabricsSeq) {
    for (const size of sizesIds) {
      variants.push({
        fabricId: fabric.id,
        size,
        variantName: makeProductName(productKey, fabric.id, size),
      });
    }
  }

  const beds: Product[] = variants.map((v) => {
    const price = prices[v.size];
    if (!price) {
      throw new Error(`no price for ${JSON.stringify(v)}`);
    }
    return {
      id: v.variantName.split("/").filter((x) => !!x)[1],
      name: v.variantName,
      displayName: productName,
      description,
      price,
      images: images[v.fabricId] || [],
      modelId: productKey,
      details: {
        $case: "dogBed",
        dogBed: {
          size: v.size,
          fabricId: v.fabricId,
          fabrics: fabricsSeq,
          sizes,
          variants,
        },
      },
    };
  });

  return beds;
}

const allProductKeys: Record<BedKey, 1> = {
  kolorado: 1,
  norka: 1,
  podushka: 1,
  chemodan: 1,
  kvadroSoft: 1,
  kvadroStrong: 1,
  lukoshkoDuo: 1,
  lukoshkoTrio: 1,
};

function min(arr: Product[]): number {
  return arr.map((s) => s.price.price).reduce((a, b) => Math.min(a, b));
}

export const all_beds_grouped = Object.keys(allProductKeys).map((key) =>
  makeProducts(
    key,
    PRODUCT_NAMES[key],
    ALL_PRICES[key],
    DESCRIPTIONS[key],
    ALL_IMAGES[key],
    ALL_SIZES[key],
    ALL_FABRICS[key],
  ),
);

export const all_beds = all_beds_grouped.flatMap((x) => x);
export const categories: Category[] = [
  {
    id: "beds",
    name: "categories/beds",
    displayName: "Лежанки",
    description: "Лежанки для питомцев",
    image: {
      src: "https://picsum.photos/300/300?random=1",
      alt: "beds category",
      name: "categories/beds",
    },
    products: all_beds.map((p) => p.name),
  },
];

export const models = all_beds_grouped.map((modelProducts): Model => {
  const p = modelProducts[0];
  return {
    description: "",
    displayName: PRODUCT_NAMES[p.modelId],
    id: p.modelId,
    images: modelProducts
      .filter(
        (product) => product.details.dogBed.size === p.details.dogBed.size,
      )
      .flatMap((product) => product.images),
    name: p.name,
    minimalPrice: min(modelProducts),
  };
});
