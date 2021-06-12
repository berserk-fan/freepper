import {
  DogBed_Variant,
  ImageData,
  Price,
  Product,
} from "@mamat14/shop-server/shop_model";
import vicFabrics, { VicFabricKey } from "configs/fabrics/vicFabrics";
import lukoshkoSizes, { LukoshkoSizeKeys } from "configs/sizes/lukoshkoSizes";
import { makeProductName } from "./commons";

function getVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of lukoshkoSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: makeProductName("lukoshkoTrio", fabric.id, size),
      });
    }
  }
  return res;
}

const variants: DogBed_Variant[] = getVariants();
const defaultSize = lukoshkoSizes[0];

export const lukoshkoTrioImages: Record<
  string,
  ImageData & { name: string }
> = {
  lukoshkoTrioFromAbove: {
    src: "/beds/lukoshko3/Dogs-7253.jpg",
    alt: "Фото серого Лукошко Трио сверху",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioFromSide: {
    src: "/beds/lukoshko3/Dogs-7254.jpg",
    alt: "Фото серого Лукошко Трио ракурс",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioWithDrops: {
    src: "/beds/lukoshko3/Dogs-7309.jpg",
    alt:
      "Фото розового Лукошко Трио с каплями на водонепроницаемой поверхности",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioWithDog: {
    src: "/beds/lukoshko3/Dogs-24838.jpg",
    alt: "Фото спереди собачки в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoAndHappyDog: {
    src: "/beds/lukoshko3/Dogs-24849.jpg",
    alt: "Фото спереди веселой собачки в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioAndPug: {
    src: "/beds/lukoshko3/Dogs-25105.jpg",
    alt: "Фото довольного мопса в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioAndHappyPug: {
    src: "/beds/lukoshko3/Dogs-25115.jpg",
    alt: "Фото довольного мопса в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioAndHappyPug2: {
    src: "/beds/lukoshko3/Dogs-25127.jpg",
    alt: "Фото довольного мопса в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
  lukoshkoTrioAndHappyPug3: {
    src: "/beds/lukoshko3/Dogs-25142.jpg",
    alt: "Фото довольного мопса в сером Лукошко Трио",
    name: makeProductName("lukoshkoTrio", "vic-93", defaultSize),
  },
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

const imagesWithFabric: Record<string, ImageData[]> = Object.fromEntries<
  ImageData[]
>(
  Object.entries(fabricToImage).map(([id, photos]: [string, ImageData[]]) => [
    id,
    photos.concat([{ src: `/fabrics/vic/${id}.JPG`, alt: "Фото ткани" }]),
  ]),
);

const prices: Record<LukoshkoSizeKeys, Price> = {
  "lukoshko-xs": { price: 1350 },
  "lukoshko-s": { price: 1450 },
  "lukoshko-m": { price: 1600 },
  "lukoshko-l": { price: 1750 },
};

const description = `
Тканина, фурнітура, наповнювачі:
 - Тканина прошита армованими нитками
 - Борти і матрац наповнені гіпоалергенним і антибактеріальним холлофайбером
 - Водовідштовхувальна тканина

«Лукошко» пошито з тканини зі спеціальним водовідштовхувальним просоченням: волога скачується і легко струшується з цього матеріалу. ⠀ 
Ми любимо її за оксамитову обробку матеріалу і повітряне наповнення. 
Матеріал має таке щільне переплетення, що навіть гостру, як ялинкові голочки шерсть, можна струсити одним рухом. 
Це ж властивість робить тканину дуже міцною і зносостійкого. Лежаночка має знімний чохол на подушечку, який можна прати в машинці.
`;

const lukoshkos: Product[] = variants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Лукошко Трио`,
  description,
  price: prices[v.sizeId],
  images: imagesWithFabric[v.fabricId],
  modelName: "lukoshkoTrio",
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: vicFabrics,
      sizes: lukoshkoSizes,
      variants,
    },
  },
}));

export default lukoshkos;
