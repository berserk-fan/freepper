import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import vicFabrics from "../fabrics/vicFabrics";
import kvadroSizes from "../sizes/kvadroSizes";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of kvadroSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `products/kvadroSoft-${fabric.id}-${size.id}`,
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();

const images: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries({
    "vic-20": [],
    "vic-21": [],
    "vic-22": [],
    "vic-32": [],
    "vic-34": [],
    "vic-36": [],
    "vic-66": [],
    "vic-70": [
      "Dogs-7152 (1).jpg",
      "Dogs-7234.jpg",
      "Dogs-7239.jpg",
      "Dogs-7278.jpg",
    ],
    "vic-80": [],
    "vic-88": [],
    "vic-93": [],
    "vic-96": [],
    "vic-100": [],
  }).map(([id, photos]: [string, ImageData[]]) => [
    id,
    photos
      .map((name) => ({
        src: `/beds/kvadro-strong/${name}.jpg`,
        alt: "фото лежанки Квадро Стронг",
      }))
      .concat([
        {
          src: `/fabrics/vic/${id}.JPG`,
          alt: "Фото ткани из которой сделана лежанка Квадро Стронг",
        },
      ]),
  ]),
);

const prices: Record<string, Price> = {
  "kvadro-xs": { price: 1350 },
  "kvadro-s": { price: 1450 },
  "kvadro-m": { price: 1550 },
  "kvadro-l": { price: 1750 },
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

const kvadroSofts: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: "Лукошко Ейфель",
  description,
  price: prices[v.sizeId],
  images: images[v.fabricId],
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
