import { Price as Price1 } from "apis/catalog";
import React from "react";

export default function Price({ price }: { price: Price1 }) {
  return <span>{`${price.price.toString()}₴`}</span>;
}
