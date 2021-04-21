import { Category, Product } from "@mamat14/shop-server/shop_model";
import { lukoshkoDuos } from "configs/beds/lukoshkoDuos";
import lukoshkoTrios from "configs/beds/lukoshkoTrios";
import chemodans from "configs/beds/chemodans";
import kvadroSofts from "configs/beds/kvadroSofts";
import kvadroStrongs from "configs/beds/kvadroStrongs";

export const shopProducts: Product[] = lukoshkoDuos
  .concat(lukoshkoTrios)
  .concat(chemodans)
  .concat(kvadroSofts)
  .concat(kvadroStrongs);

export const categories: Category[] = [
  {
    id: "beds",
    name: "categories/beds",
    displayName: "Лежанки",
    description: "Лежанки для питомцев",
    image: {
      src: "https://picsum.photos/300/300?random=1",
      alt: "beds category",
    },
    products: shopProducts.map((p) => p.name),
  },
];
