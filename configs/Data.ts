import {
  Category,
  DogBed_Variant,
  Fabric, ImageData, Price,
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

const lukoshkoFabrics: Fabric[] = [
  {
    id: "avro-500",
    displayName: "Avro 500",
    description: "Ткань avro 500",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Ткань avro 500",
    },
  },
  {
    id: "avro-600",
    displayName: "Avro 600",
    description: "Ткань avro 600",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Ткань avro 500",
    },
  },
  {
    id: "avro-700",
    displayName: "Avro 700",
    description: "Ткань avro 700",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Ткань avro 500",
    },
  },
  {
    id: "avro-800",
    displayName: "Avro 800",
    description: "Ткань avro 800",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Ткань avro 500",
    },
  },
  {
    id: "avro-900",
    displayName: "Avro 900",
    description: "Ткань avro 900",
    image: {
      src: "https://picsum.photos/30/30?random=1",
      alt: "Ткань avro 500",
    },
  },
];

function getLukoshkoVariants(): DogBed_Variant[] {
  const res: DogBed_Variant[] = [];
  for (const fabric of lukoshkoFabrics) {
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

type Customizations = {price: Price, images: ImageData[]}

const luckoshkoCustomizations: Record<string, Customizations> = Object.fromEntries(getLukoshkoVariants().map((v,i) =>
      [v.variantName, {
        images: ["/Dogs-7248.jpg", "/Dogs-7078.jpg", "/Dogs-7133.jpg"].slice(i % 3).map(image => (
            {
              src: image,
              alt: "фото лежанки",
            }
        )),
        price: {price: Math.floor(970 + Math.random() * 100)}
      }]));

const lukoshkoVariants: DogBed_Variant[] = getLukoshkoVariants();

export const shopProducts: Product[] = lukoshkoVariants.map(v => ({
    id: v.variantName.split("/").filter(x => !!x)[1],
    name: v.variantName,
    displayName: `Лукошко - ${lukoshkoFabrics.find(s => s.id == v.fabricId).displayName} - ${lukoshkoSizes.find(s => s.id == v.sizeId).displayName}`,
    description: "Хорошая лежанка",
    price: luckoshkoCustomizations[v.variantName].price,
    images: luckoshkoCustomizations[v.variantName].images,
    details: {
      $case: "dogBed",
      dogBed: {
        sizeId: v.sizeId,
        fabricId: v.fabricId,
        fabrics: lukoshkoFabrics,
        sizes: lukoshkoSizes,
        variants: lukoshkoVariants,
      },
    },
  }),
);

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
