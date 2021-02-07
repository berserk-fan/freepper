import { Box, Container, Grid } from "@material-ui/core";
import ItemView from "../../../../components/Shop/ItemView";
import LayoutWithHeaderAndFooter from "../../../../components/Layout/LayoutWithHeaderAndFooter";
import { Category, Product } from "@mamat14/shop-server/shop_model";
import React from "react";
import { shopClient } from "../../../../store";
import { GetStaticProps } from "next";
import {tmpProducts} from "../../../../../configs/tmpProducts";

export default function Shop({
  products,
  categoryName,
}: {
  products: Product[];
  categoryName: string;
}) {
  return (
    <LayoutWithHeaderAndFooter>
      <Container>
        <Box paddingTop={1}>
          <Grid container={true} spacing={3} justify={"space-between"}>
            {products.map((item) => (
              <Grid key={item.id} item={true} xs={12} sm={6} md={4} lg={3}>
                <ItemView
                  productRef={`/${categoryName}/${item.name}`}
                  product={item}
                  className={"mx-auto"}
                />
              </Grid>
            ))}
          </Grid>
        </Box>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  // const categoryId = context.params.categoryId;
  // const category = await shopClient.getCategory({
  //   name: `categories/${categoryId}`,
  // });
  // const products = await Promise.all(
  //   category.products.map((pName) => shopClient.getProduct({ name: pName }))
  // );
  const products = tmpProducts;
  const category = "categories/beds";
  return {
    props: {
      products: products,
      categoryName: category,
    },
  };
};

export async function getStaticPaths() {
  const categories = ["beds"];
  const paths = categories.map((c) => ({ params: { categoryId: c } }));

  return { paths, fallback: false };
}
