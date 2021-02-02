import {
  Category,
  DogBed_Variant,
  Fabric,
  ImageData,
  Price,
  Product,
  Size,
} from "@mamat14/shop-server/shop_model";

const lukoshkoSizes: Size[] = [
  {
    id: "lukoshko-1",
    displayName: "XS",
    description: "Диаметр 50см",
  },
  {
    id: "lukoshko-2",
    displayName: "S",
    description: "Диаметр 60см",
  },
  {
    id: "lukoshko-3",
    displayName: "M",
    description: "Диаметр 70см",
  },
  {
    id: "lukoshko-4",
    displayName: "L",
    description: "Диаметр 80см",
  },
];

const kvadroSizes: Size[] = [
  {
    id: "kvadro-1",
    displayName: "XS",
    description: "50x40см",
  },
  {
    id: "kvadro-2",
    displayName: "S",
    description: "60x40см",
  },
  {
    id: "kvadro-3",
    displayName: "M",
    description: "70x50см",
  },
  {
    id: "kvadro-4",
    displayName: "L",
    description: "90х60см",
  },
];

const chemodanSizes: Size[] = [
  {
    id: "chemodan-1",
    displayName: "M",
    description: "70x50см",
  },
  {
    id: "chemodan-2",
    displayName: "L",
    description: "90х60см",
  },
  {
    id: "chemodan-3",
    displayName: "XL",
    description: "110х70см",
  },
  {
    id: "kvadro-4",
    displayName: "XXL",
    description: "120х80см",
  },
];

const vicFabrics: Fabric[] = [
  {
    id: "vic-20",
    displayName: "Молочный",
    description: "",
    image: {
      src: "/fabrics/vic/vic-20.JPG",
      alt: "Молочная ткань",
    },
  },
  {
    id: "vic-21",
    displayName: "Капучино",
    description: "",
    image: {
      src: "/fabrics/vic/vic-21.JPG",
      alt: "Ткань цвета капучино",
    },
  },
  {
    id: "vic-22",
    displayName: "Olive Grey",
    description: "",
    image: {
      src: "/fabrics/vic/vic-22.JPG",
      alt: "Ткань оливково серого цвета",
    },
  },
  {
    id: "vic-32",
    displayName: "Хиллари",
    description: "",
    image: {
      src: "/fabrics/vic/vic-32.JPG",
      alt: "Ткань цвета хиллари",
    },
  },
  {
    id: "vic-34",
    displayName: "Кофе",
    description: "",
    image: {
      src: "/fabrics/vic/vic-34.JPG",
      alt: "Ткань цвета кофе",
    },
  },
  {
    id: "vic-36",
    displayName: "Шоколад",
    description: "",
    image: {
      src: "/fabrics/vic/vic-36.JPG",
      alt: "Ткань цвета шоколад",
    },
  },
  {
    id: "vic-66",
    displayName: "Орхидея",
    description: "",
    image: {
      src: "/fabrics/vic/vic-66.JPG",
      alt: "Ткань цвета орхидея",
    },
  },
  {
    id: "vic-70",
    displayName: "Васаби",
    description: "",
    image: {
      src: "/fabrics/vic/vic-70.JPG",
      alt: "Ткань цвета васаби",
    },
  },
  {
    id: "vic-80",
    displayName: "Аквамарин",
    description: "",
    image: {
      src: "/fabrics/vic/vic-80.JPG",
      alt: "Ткань цвета аквамарин",
    },
  },
  {
    id: "vic-88",
    displayName: "Синий зодиак",
    description: "",
    image: {
      src: "/fabrics/vic/vic-88.JPG",
      alt: "Ткань цвета синий зодиак",
    },
  },
  {
    id: "vic-93",
    displayName: "Серебро",
    description: "",
    image: {
      src: "/fabrics/vic/vic-93.JPG",
      alt: "Ткань цвета серебро",
    },
  },
  {
    id: "vic-100",
    displayName: "Черный",
    description: "",
    image: {
      src: "/fabrics/vic/vic-100.JPG",
      alt: "Ткань цвета черный",
    },
  },
];

const avFabrics: Fabric[] = [
  {
    id: "av-01",
    displayName: "Алюминий",
    description: "",
    image: {
      src: "/fabrics/av/av-01.png",
      alt: "Ткань цвета алюминий",
    },
  },
  {
    id: "av-02",
    displayName: "Серый туман",
    description: "",
    image: {
      src: "/fabrics/av/av-02.png",
      alt: "Ткань цвета серый туман",
    },
  },
  {
    id: "av-04",
    displayName: "Сантал",
    description: "",
    image: {
      src: "/fabrics/av/av-04.png",
      alt: "Ткань цвета сантал",
    },
  },
  {
    id: "av-06",
    displayName: "Какао",
    description: "",
    image: {
      src: "/fabrics/av/av-06.png",
      alt: "Ткань цвета какао",
    },
  },
  {
    id: "av-10",
    displayName: "Пыльная роза",
    description: "",
    image: {
      src: "/fabrics/av/av-10.png",
      alt: "Ткань цвета пыльная роза",
    },
  },
  {
    id: "av-11",
    displayName: "Бирюза",
    description: "",
    image: {
      src: "/fabrics/av/av-11.png",
      alt: "Ткань цвета Бирюза",
    },
  },
  {
    id: "av-12",
    displayName: "Изумруд",
    description: "",
    image: {
      src: "/fabrics/av/av-12.png",
      alt: "Ткань цвета изумруд",
    },
  },
  {
    id: "av-13",
    displayName: "Гольфстрим",
    description: "",
    image: {
      src: "/fabrics/av/av-13.png",
      alt: "Ткань цвета васаби",
    },
  },
  {
    id: "av-14",
    displayName: "Астронавт",
    description: "",
    image: {
      src: "/fabrics/av/av-14.png",
      alt: "Ткань цвета астронавт",
    },
  },
  {
    id: "av-15",
    displayName: "Серебро",
    description: "",
    image: {
      src: "/fabrics/av/av-15.png",
      alt: "Ткань цвета серебро",
    },
  },
  {
    id: "av-17",
    displayName: "Серый",
    description: "",
    image: {
      src: "/fabrics/av/av-17.png",
      alt: "Ткань серого цвета",
    },
  },
  {
    id: "av-18",
    displayName: "Черный",
    description: "",
    image: {
      src: "/fabrics/av/av-18.png",
      alt: "Черная ткань",
    },
  },
];

function getLukoshkoVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of vicFabrics) {
    for (const size of lukoshkoSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `products/lukoshko-${fabric.id}-${size.id}`,
      });
    }
  }
  return res;
}

const lukoshkoVariants: DogBed_Variant[] = getLukoshkoVariants();

const lukoshkoPrices: Record<string, Price> = {
  "lukoshko-1": { price: 950 },
  "lukoshko-2": { price: 1050 },
  "lukoshko-3": { price: 1350 },
  "lukoshko-4": { price: 1550 },
};

// const lukoshkoImages: Record<string, ImageData[]> = {
//   "av-01": [],
//   "av-02": [],
//   "av-04": [],
//   "av-06": [],
//   "av-10": [],
//   "av-11": [],
//   "av-12": [],
//   "av-13": [],
//   "av-14": [],
//   "av-15": [],
//   "av-17": [],
//   "av-18": []
// };

const lukoshkoImages: Record<string, ImageData[]> = Object.fromEntries(
  Object.entries({
    "vic-20": [
      { src: "/beds/lukoshko2/Dogs-7043.jpg", alt: "фото лежанки Лукошко" },
    ],
    "vic-21": [
      { src: "/beds/lukoshko2/Dogs-7043.jpg", alt: "фото лежанки Лукошко" },
    ],
    "vic-22": [],
    "vic-32": [],
    "vic-34": [],
    "vic-36": [],
    "vic-66": [],
    "vic-70": [],
    "vic-80": [],
    "vic-88": [],
    "vic-93": [],
    "vic-96": [],
    "vic-100": [],
  }).map(([id, photos]) => [
    id,
    photos.concat([{ src: "/beds/lukoshko2/Dogs-7043.jpg", alt: "qwe" }]),
  ])
);

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

const lukoshkos: Product[] = lukoshkoVariants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Лукошко`,
  description: lukoshkoDescription,
  price: lukoshkoPrices[v.sizeId],
  images: lukoshkoImages[v.fabricId],
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

export const shopProducts: Product[] = lukoshkos;

export const category: Category = {
  id: "beds",
  name: "categories/beds",
  displayName: "Лежанки",
  description: "Лежанки для питомцев",
  image: {
    src: "https://picsum.photos/300/300?random=1",
    alt: "beds category",
  },
  products: lukoshkos.map((p) => p.name),
};
