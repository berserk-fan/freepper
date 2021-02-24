import {VicFabricKey} from "../fabrics/vicFabrics";
import {AvFabricKeys} from "../fabrics/avFabrics";
import {ChemodanSizeKeys} from "../sizes/chemodanSizes";
import {KvadroSizeKeys} from "../sizes/kvadroSizes";
import {LukoshkoSizeKeys} from "../sizes/lukoshkoSizes";

export type FabricKey = VicFabricKey | AvFabricKeys;
export type SizeKey = ChemodanSizeKeys | KvadroSizeKeys | LukoshkoSizeKeys;
export type ProductKey = "lukoshkoDuo" | "lukoshkoTrio" | "chemodan" | "kvadroSoft"  | "kvadroStrong"

export function makeProductName(name: ProductKey, fabric: FabricKey, size: SizeKey) {
    return `products/${name}-${fabric}-${size}`
}
