import React from "react";
import { Product } from "apis/catalog";
import { GetStaticPaths, GetStaticProps } from "next";
import { shopClient } from "store";
import FoundProductPage from "components/ProductPage/FoundProductPage";
import LayoutWithHeaderAndFooter from "components/Layout/LayoutWithHeaderAndFooter";
import Box from "@material-ui/core/Box/Box";

export default function ProductPage({
  product,
  categoryName,
}: {
  product: Product | null;
  categoryName: string;
}) {
  return (
    <LayoutWithHeaderAndFooter
      showValueProp
      breadcrumbsOverrides={{ [product.id]: product.displayName }}
    >
      <Box>
        {!product && "Product not found"}
        {product && (
          <FoundProductPage categoryName={categoryName} product={product} />
        )}
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const { productId } = context.params;
  const categoryName = `categories/${context.params.categoryId}`;
  const product = await shopClient.getProduct({
    name: `products/${productId}`,
  });
  return { props: { product: product || null, categoryName } };
};

export const getStaticPaths: GetStaticPaths = async () => {
  // TODO replace with listCategories or listProducts or listModels
  const categories = ["categories/beds", "categories/ammo"];
  const paths = (
    await Promise.all(
      categories.map((c) => shopClient.getCategory({ name: c })),
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
