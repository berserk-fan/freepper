import { VicFabricKey } from "configs/fabrics/vicFabrics";
import { AvFabricKeys } from "configs/fabrics/avFabrics";
import { ChemodanSizeKeys } from "configs/sizes/chemodanSizes";
import { KvadroSizeKeys } from "configs/sizes/kvadroSizes";
import { LukoshkoSizeKeys } from "configs/sizes/lukoshkoSizes";
import {Size} from "@mamat14/shop-server/shop_model";

export type FabricKey = VicFabricKey | AvFabricKeys;
export type SizeKey = ChemodanSizeKeys | KvadroSizeKeys | LukoshkoSizeKeys;
export type ProductKey =
  | "lukoshkoDuo"
  | "lukoshkoTrio"
  | "chemodan"
  | "kvadroSoft"
  | "kvadroStrong";

export function makeProductName(
  name: ProductKey,
  fabric: FabricKey,
  size: Size,
) {
  return `products/${name}-${fabric}-${size.id}`;
}
