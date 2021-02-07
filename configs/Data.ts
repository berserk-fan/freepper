import {Category, Product,} from "@mamat14/shop-server/shop_model";
import {lukoshkoDuos} from "./beds/lukoshkoDuos";
import lukoshkoTrios from "./beds/lukoshkoTrios"
import chemodans from "./beds/chemodans";
import kvadroSofts from "./beds/kvadroSofts";
import kvadroStrongs from "./beds/kvadroStrongs";

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
  {
    id: "beds",
    name: "categories/groupedBeds",
    displayName: "Лежанки",
    description: "Лежанки для питомцев",
    image: {
      src: "https://picsum.photos/300/300?random=1",
      alt: "beds category",
    },
    products: [],
  },
];
