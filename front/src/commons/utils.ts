import { Product, Product_Price } from "apis/product.pb";
import { Money } from "apis/money.pb";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function checkExhaustive(_: never): never {
  throw new Error("Not exhaustive switch case");
}

export function priceToString(s: Product_Price, count: number = 1): string {
  return `${s.standard.amount * count} ₴`;
}

export function getCurrentPrice(s: Product_Price): Money {
  return s.standard;
}

// https://github.com/vercel/next.js/discussions/11209
export function removeUndefined<T>(t: T): T {
  return JSON.parse(JSON.stringify(t));
}

export function indexProducts(products: Product[]) {
  return Object.fromEntries(
    products.map((p) => [
      p.parameterIds.sort((a, b) => a.localeCompare(b)).join(),
      p,
    ]),
  );
}
