import {Size} from "@mamat14/shop-server/shop_model";

export type KvadroSizeKeys = "kvadro-xs" | "kvadro-s" | "kvadro-m" | "kvadro-l"

const sizes: Record<KvadroSizeKeys, Size & {id: KvadroSizeKeys}> = {
  "kvadro-xs": {
    id: "kvadro-xs",
    displayName: "XS",
    description: "50x40см",
  },
  "kvadro-s": {
    id: "kvadro-s",
    displayName: "S",
    description: "60x40см",
  },
  "kvadro-m": {
    id: "kvadro-m",
    displayName: "M",
    description: "70x50см",
  },
  "kvadro-l": {
    id: "kvadro-l",
    displayName: "L",
    description: "90х60см",
  },
};

export default Object.values(sizes);
