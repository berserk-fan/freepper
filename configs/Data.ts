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
    id: "1",
    displayName: "XS",
    description: "80x40",
  },
  {
    id: "2",
    displayName: "S",
    description: "90x60",
  },
  {
    id: "3",
    displayName: "M",
    description: "120x90",
  },
  {
    id: "4",
    displayName: "L",
    description: "150x100",
  },
];


const lukoshkoSoftFabrics: Fabric[] = [
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
    displayName: "Бедрок",
    description: "",
    image: {
      src: "/fabrics/vic/vic-100.JPG",
      alt: "Ткань цвета бедрок",
    },
  }
];

function getLukoshkoVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of lukoshkoSoftFabrics) {
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

type Customizations = { price: Price; images: ImageData[] };
const lukoshkoCustomizations: Record<
  string,
  Customizations
> = Object.fromEntries(
  lukoshkoVariants.map((v, i) => [
    v.variantName,
    {
      images: [
        ...["/IMG_4241.HEIC", "/Dogs-7078.jpg", "/Dogs-7255.jpg"]
          .slice(i % 2)
          .map((image) => ({
            src: image,
            alt: "фото лежанки",
          })),
      ],
      price: { price: Math.floor(970 + Math.random() * 100) },
    },
  ])
);

export const shopProducts: Product[] = lukoshkoVariants.map((v) => ({
  id: v.variantName.split("/").filter((x) => !!x)[1],
  name: v.variantName,
  displayName: `Лукошко`,
  description:
    "Таким образом постоянное информационно пропагандистское обеспечение нашей деятельности способствует подготовки и реализации существенных финансовых и административных условий. Идейные соображения высшего порядка, а также укрепление и развитие структуры.",
  price: lukoshkoCustomizations[v.variantName].price,
  images: lukoshkoCustomizations[v.variantName].images,
  details: {
    $case: "dogBed",
    dogBed: {
      sizeId: v.sizeId,
      fabricId: v.fabricId,
      fabrics: lukoshkoSoftFabrics,
      sizes: lukoshkoSizes,
      variants: lukoshkoVariants,
    },
  },
}));

export const category: Category = {
  id: "beds",
  name: "categories/beds",
  displayName: "Лежанки",
  description: "Лежанки для питомцев",
  image: {
    src: "https://picsum.photos/300/300?random=1",
    alt: "beds category",
  },
  products: shopProducts.map((p) => p.name),
};
