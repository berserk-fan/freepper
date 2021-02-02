import React from "react";
import { shopClient } from "../../../../store";
import { Product } from "@mamat14/shop-server/shop_model";
import { GetStaticPaths, GetStaticProps } from "next";
import { Box, Container } from "@material-ui/core";
import FoundProductPage from "../../../../components/ProductPage/FoundProductPage";
import LayoutWithHeaderAndFooter from "../../../../components/Layout/LayoutWithHeaderAndFooter";
import theme from "../../../../theme";

export default function ProductPage({
  product,
  categoryName,
}: {
  product: Product | null;
  categoryName: string;
}) {
  return (
    <LayoutWithHeaderAndFooter>
      <Box padding={1}>
        {product == null ? "Product not found" : false}
        {product && (
          <FoundProductPage categoryName={categoryName} product={product} />
        )}
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const productId = context.params.productId;
  const categoryName = `categories/${context.params.categoryId}`;
  const product = await shopClient.getProduct({
    name: `products/${productId}`,
  });
  return { props: { product: product || null, categoryName } };
};

export const getStaticPaths: GetStaticPaths = async () => {
  const categories = ["categories/beds"];
  const paths = (
    await Promise.all(
      categories.map((c) => shopClient.getCategory({ name: c }))
    )
  )
    .flatMap((c) => c.products.map((p) => [c.id, p]))
    .map(([cId, pName]) => ({
      params: {
        categoryId: cId,
        productId: pName.substring("products/".length),
      },
    }));
  return { paths, fallback: false };
};
