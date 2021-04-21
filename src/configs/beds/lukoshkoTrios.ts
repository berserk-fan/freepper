import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import vicFabrics, {VicFabricKey} from "configs/fabrics/vicFabrics";
import lukoshkoSizes, {LukoshkoSizeKeys} from "configs/sizes/lukoshkoSizes";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of lukoshkoSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `products/lukoshkoTrio-${fabric.id}-${size.id}`,
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();


export const lukoshkoTrioImages: Record<string, ImageData & {name: string}> = {
    "lukoshkoTrioFromAbove": {
        src: "/beds/lukoshko3/Dogs-7253.jpg",
        alt: "Фото серого Лукошко Трио сверху",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioFromSide": {
        src: "/beds/lukoshko3/Dogs-7254.jpg",
        alt: "Фото серого Лукошко Трио ракурс",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioWithDrops": {
        src: "/beds/lukoshko3/Dogs-7309.jpg",
        alt: "Фото розового Лукошко Трио с каплями на водонепроницаемой поверхности",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioWithDog": {
        src: "/beds/lukoshko3/Dogs-24838.jpg",
        alt: "Фото спереди собачки в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoAndHappyDog": {
        src: "/beds/lukoshko3/Dogs-24849.jpg",
        alt: "Фото спереди веселой собачки в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioAndPug": {
        src: "/beds/lukoshko3/Dogs-25105.jpg",
        alt: "Фото довольного мопса в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioAndHappyPug": {
        src: "/beds/lukoshko3/Dogs-25115.jpg",
        alt: "Фото довольного мопса в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioAndHappyPug2": {
        src: "/beds/lukoshko3/Dogs-25127.jpg",
        alt: "Фото довольного мопса в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    },
    "lukoshkoTrioAndHappyPug3": {
        src: "/beds/lukoshko3/Dogs-25142.jpg",
        alt: "Фото довольного мопса в сером Лукошко Трио",
        name: "products/lukoshkoTrio-vic-96-lukoshko-xs",
    }
};

const image = lukoshkoTrioImages;
const fabricToImage: Record<VicFabricKey, ImageData[]> = {
    "vic-20": [],
    "vic-21": [],
    "vic-22": [],
    "vic-32": [],
    "vic-34": [],
    "vic-36": [],
    "vic-66": [],
    "vic-70": [],
    "vic-80": [],
    "vic-88": [],
    "vic-93": [
        image.lukoshkoTrioFromAbove,
        image.lukoshkoTrioFromSide,
        image.lukoshkoTrioWithDrops,
        image.lukoshkoTrioWithDog,
        image.lukoshkoAndHappyDog,
        image.lukoshkoTrioAndPug,
        image.lukoshkoTrioAndHappyPug,
        image.lukoshkoTrioAndHappyPug2,
        image.lukoshkoTrioAndHappyPug3,
    ],
    "vic-100": [],
};

const imagesWithFabric: Record<string, ImageData[]> = Object.fromEntries<ImageData[]>(
  Object.entries(fabricToImage).map(([id, photos]: [string, ImageData[]]) =>
      [id, photos.concat([{ src: `/fabrics/vic/${id}.JPG`, alt: "Фото ткани" }])])
);

const prices: Record<LukoshkoSizeKeys, Price> = {
  "lukoshko-xs": { price: 1350 },
  "lukoshko-s": { price: 1450 },
  "lukoshko-m": { price: 1550 },
  "lukoshko-l": { price: 1750 },
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

const lukoshkos: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Лукошко Ейфель`,
  description: description,
  price: prices[v.sizeId],
  images: imagesWithFabric[v.fabricId],
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: vicFabrics,
      sizes: lukoshkoSizes,
      variants: variants,
    },
  },
}));

export default lukoshkos;
