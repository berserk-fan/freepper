import { Container, Grid, Typography } from "@material-ui/core";
import ItemView from "../../components/ItemView";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../../configs/Data";
import { Category, Product } from "@mamat14/shop-server/shop_model";
import React from "react";

export async function getStaticProps() {
  const shopClient = new ShopClient({
    categories: [category],
    products: shopProducts,
  });
  const request = {
    parent: "/categories/beds/products",
    pageSize: 25,
    pageToken: "",
  };
  const { products } = await shopClient.listProducts(request);
  const categories = [
    await shopClient.getCategory({ name: "/categories/beds" }),
  ];
  return {
    props: {
      products: products,
      categories: categories,
    },
  };
}

export default function Shop({
  products,
  categories,
}: {
  products: Product[];
  categories: Category[];
}) {
  const currentCategory = categories[0];
  return (
    <LayoutWithHeader>
      <Container>
        <Typography align={"center"} variant={"h3"}>
          {currentCategory.displayName}
        </Typography>
        <Grid container={true} spacing={3} justify={"space-between"}>
          {products.map((item) => (
            <Grid key={item.id} item={true} xs={12} sm={6} md={3}>
              <ItemView product={item} className={"mx-auto"} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </LayoutWithHeader>
  );
}
