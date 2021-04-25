import { Size } from "@mamat14/shop-server/shop_model";

export type ChemodanSizeKeys =
  | "chemodan-m"
  | "chemodan-l"
  | "chemodan-xl"
  | "chemodan-xxl";

const sizes: Record<ChemodanSizeKeys, Size & { id: ChemodanSizeKeys }> = {
  "chemodan-m": {
    id: "chemodan-m",
    displayName: "M",
    description: "70x50см",
    weight: 1,
  },
  "chemodan-l": {
    id: "chemodan-l",
    displayName: "L",
    description: "90х60см",
    weight: 2,
  },
  "chemodan-xl": {
    id: "chemodan-xl",
    displayName: "XL",
    description: "110х70см",
    weight: 3,
  },
  "chemodan-xxl": {
    id: "chemodan-xxl",
    displayName: "XXL",
    description: "120х80см",
    weight: 4,
  },
};

export default Object.values(sizes);
