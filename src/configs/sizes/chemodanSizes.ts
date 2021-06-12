import { Size } from "@mamat14/shop-server/shop_model";

export type ChemodanSizeKeys =
  | "chemodan-s"
  | "chemodan-m"
  | "chemodan-l"
  | "chemodan-xl"
  | "chemodan-xxl";

const sizes: Record<ChemodanSizeKeys, Size & { id: ChemodanSizeKeys }> = {
  "chemodan-s": {
    id: "chemodan-s",
    displayName: "S",
    description: "70x50см",
    weight: 1,
  },
  "chemodan-m": {
    id: "chemodan-m",
    displayName: "M",
    description: "90x60см",
    weight: 2,
  },
  "chemodan-l": {
    id: "chemodan-l",
    displayName: "L",
    description: "110x70см",
    weight: 3,
  },
  "chemodan-xl": {
    id: "chemodan-xl",
    displayName: "XL",
    description: "120x80см",
    weight: 4,
  },
  "chemodan-xxl": {
    id: "chemodan-xxl",
    displayName: "XXL",
    description: "140x100см",
    weight: 5,
  },
};

export default Object.values(sizes);
