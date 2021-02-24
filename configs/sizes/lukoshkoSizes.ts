import { Size } from "@mamat14/shop-server/shop_model";

export type LukoshkoSizeKeys = "lukoshko-xs" | "lukoshko-s" | "lukoshko-m" | "lukoshko-l"

const sizes: Record<LukoshkoSizeKeys, Size & {id: LukoshkoSizeKeys}> = {
    "lukoshko-xs": {
        id: "lukoshko-xs",
        displayName: "XS",
        description: "Диаметр 50см",
    },
    "lukoshko-s": {
        id: "lukoshko-s",
        displayName: "S",
        description: "Диаметр 60см",
    },
    "lukoshko-m": {
        id: "lukoshko-m",
        displayName: "M",
        description: "Диаметр 70см",
    },
    "lukoshko-l": {
        id: "lukoshko-l",
        displayName: "L",
        description: "Диаметр 80см",
    },
};

export default Object.values(sizes)
