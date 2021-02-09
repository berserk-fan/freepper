import { Box, Container, Grid } from "@material-ui/core";
import ItemView from "../../../../components/Shop/ItemView";
import LayoutWithHeaderAndFooter from "../../../../components/Layout/LayoutWithHeaderAndFooter";
import React from "react";
import { GetStaticProps } from "next";
import {
  TmpGroupedProduct,
  tmpProducts,
} from "../../../../../configs/tmpProducts";

export default function Shop({
  products,
  categoryName,
}: {
  products: TmpGroupedProduct[];
  categoryName: string;
}) {
  return (
    <LayoutWithHeaderAndFooter>
      <Container>
        <Box paddingTop={1}>
          <Grid container={true} spacing={3} justify={"space-between"}>
            {products.map((item, idx) => (
              <Grid key={item.id} item={true} xs={12} sm={6} md={4} lg={3}>
                <ItemView
                  categoryName={categoryName}
                  product={item}
                  className={"mx-auto"}
                  priority={idx === 0}
                />
              </Grid>
            ))}
          </Grid>
        </Box>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async () => {
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
