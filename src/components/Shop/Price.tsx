import { Price as PPrice } from "@mamat14/shop-server/shop_model";
import Typography from "@material-ui/core/Typography";
import React from "react";

export default function Price({ price }: { price: PPrice }) {
  return <Typography variant={"h5"}>{price.price + " ₴"}</Typography>;
}
