import { Price as Price1 } from "@mamat14/shop-server/shop_model";

export default function Price({ price }: { price: Price1 }) {
  return <span>{price.price.toString() + "₴"}</span>;
}
