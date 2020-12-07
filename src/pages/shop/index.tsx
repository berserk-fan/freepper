import { Box, Container, Grid, Typography } from "@material-ui/core";
import ItemView from "../../components/CartItem/ItemView";
import LayoutWithHeader from "../../components/Layout/LayoutWithHeader";
import ShopClient from "@mamat14/shop-server";
import { category, shopProducts } from "../../../configs/Data";
import { Category, Product } from "@mamat14/shop-server/shop_model";
import React, { useState } from "react";
import ShopControls from "../../components/Shop/ShopControls";

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
  const [curProducts, setProducts] = useState(products);

  return (
    <LayoutWithHeader>
      <Container>
        <Box marginTop={1}>
          <Typography align={"center"} variant={"h3"}>
            {currentCategory.displayName}
          </Typography>
        </Box>
        <ShopControls products={curProducts} setProducts={setProducts} />
        <Grid container={true} spacing={3} justify={"space-between"}>
          {curProducts.map((item) => (
            <Grid key={item.id} item={true} xs={12} sm={6} md={3}>
              <ItemView product={item} className={"mx-auto"} />
            </Grid>
          ))}
        </Grid>
      </Container>
    </LayoutWithHeader>
  );
}
