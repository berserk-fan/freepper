// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { Product_Price } from "apis/product.pb";
import { Money } from "apis/money.pb";

export function checkExhaustive(_: never): never {
  throw new Error("Not exhaustive switch case");
}

export function priceToString(s: Product_Price, count: number = 1): string {
  return `${s.standard.amount * count} ₴`;
}

export function getCurrentPrice(s: Product_Price): Money {
  return s.standard;
}
