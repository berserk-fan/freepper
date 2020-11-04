import React, { useEffect, useState } from "react";
import { Product } from "@mamat14/shop-server/shop_model";
import { cartStore } from "../../store";
import Cart, { CartState, requestCartProducts } from "./Cart";
import retry from "promise-retry";

type CartNoPropsState = "PENDING" | "COMPLETED" | "FAILED";
export default function CartNoProps({
  initialProducts = {},
}: {
  initialProducts?: Record<string, Product>;
}) {
  const [products, setProducts] = useState<Record<string, Product>>(
    initialProducts
  );
  const [state, setState] = useState<CartNoPropsState>("PENDING");

  function fetchProducts() {
    if (state === "PENDING") {
      console.log("fetching");
      retry((retry) =>
        requestCartProducts(cartStore.getState())
          .then((products) => {
            setProducts(products);
            setState("COMPLETED");
          })
          .catch(retry)
      ).catch((err) => {
        console.error(err);
        setState("FAILED");
      });
    }
  }

  cartStore.subscribe(() => {
    setState("PENDING");
    fetchProducts();
  });

  useEffect(() => {
    fetchProducts();
  }, []);

  return <Cart products={products} />;
}
