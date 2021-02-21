import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import avFabrics from "../fabrics/avFabrics";
import kvadroSizes from "../sizes/kvadroSizes";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of avFabrics) {
    for (const size of kvadroSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `products/kvadroStrong-${fabric.id}-${size.id}`,
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();

const images: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries({
    "av-01": [],
    "av-02": [
      "IMG_7827.jpg",
      "IMG_7828.JPG",
      "IMG_7831.JPG",
      "IMG_7831.JPG",
      "IMG_7833.JPG",
    ],
    "av-04": [],
    "av-06": [],
    "av-07": [],
    "av-10": [],
    "av-11": [
      "Dogs-7248.jpg",
      "Dogs-7249.jpg",
      "Dogs-7251.jpg",
      "Dogs-7326.jpg",
      "Dogs-7332.jpg",
      "Dogs-24875.jpg",
      "Dogs-24890jpg",
    ],
    "av-12": [],
    "av-13": [],
    "av-14": [],
    "av-15": [],
    "av-17": [],
    "av-18": [],
  }).map(([id, photos]: [string, ImageData[]]) => [
    id,
    photos
      .map((name) => ({
        src: `/beds/kvadro-strong/${name}`,
        alt: "фото лежанки Квадро стронг",
      }))
      .concat([
        {
          src: `/fabrics/av/${id}.png`,
          alt: "Фото ткани лежанки квадро стронг",
        },
      ]),
  ]),
);

const prices: Record<string, Price> = {
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
  displayName: "Чемодан",
  description,
  price: prices[v.sizeId],
  images: images[v.fabricId],
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
