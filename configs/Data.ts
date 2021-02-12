import { Category, Product } from "@mamat14/shop-server/shop_model";
import { lukoshkoDuos } from "./beds/lukoshkoDuos";
import lukoshkoTrios from "./beds/lukoshkoTrios";
import chemodans from "./beds/chemodans";
import kvadroSofts from "./beds/kvadroSofts";
import kvadroStrongs from "./beds/kvadroStrongs";

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
