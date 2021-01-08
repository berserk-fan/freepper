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
    id: "500",
    displayName: "Красный",
    description: "Красный",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Красный",
    },
  },
  {
    id: "600",
    displayName: "Розовый",
    description: "Розовый",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Красный",
    },
  },
  {
    id: "700",
    displayName: "Синий",
    description: "Синий",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Красный",
    },
  },
  {
    id: "800",
    displayName: "Зеленый",
    description: "Зеленый",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Зеленый",
    },
  },
  {
    id: "900",
    displayName: "Синий",
    description: "Синий",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Синий",
    },
  },
];

function getLukoshkoVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of lukoshkoSoftFabrics) {
    for (const size of lukoshkoSizes) {
      res.push({
        fabricId: fabric.id,
        sizeId: size.id,
        variantName: `/products/lukoshko-${fabric.id}-${size.id}`,
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
        ...["/Dogs-7248.jpg", "/Dogs-7078.jpg", "/Dogs-7133.jpg"]
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
  description: "Хорошая лежанка",
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
  name: `/categories/beds`,
  displayName: "Лежанки",
  description: "Лежанки для питомцев",
  image: {
    src: "https://picsum.photos/300/300?random=1",
    alt: "beds category",
  },
  products: shopProducts.map((p) => p.name),
};
