import {Size} from "@mamat14/shop-server/shop_model";

export type ChemodanSizeKeys = "chemodan-m" | "chemodan-l" | "chemodan-xl" | "chemodan-xxl"

const sizes: Record<ChemodanSizeKeys, Size & {id: ChemodanSizeKeys}> = {
  "chemodan-m": {
    id: "chemodan-m",
    displayName: "M",
    description: "70x50см",
  },
  "chemodan-l": {
    id: "chemodan-l",
    displayName: "L",
    description: "90х60см",
  },
  "chemodan-xl": {
    id: "chemodan-xl",
    displayName: "XL",
    description: "110х70см",
  },
  "chemodan-xxl": {
    id: "chemodan-xxl",
    displayName: "XXL",
    description: "120х80см",
  },
};

export default Object.values(sizes)
