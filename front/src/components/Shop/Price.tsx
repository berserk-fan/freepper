import React from "react";
import { Money } from "apis/money.pb";

export default function Price({ price }: { price: Money }) {
  return <span>{`${price.amount} ₴`}</span>;
}
