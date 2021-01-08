import React from "react";
import { shopClient } from "../../store";
import { Product } from "@mamat14/shop-server/shop_model";
import { GetStaticProps } from "next";
import { Container } from "@material-ui/core";
import Header from "../../components/Layout/Header/Header";
import FoundProductPage from "../../components/ProductPage/FoundProductPage";

export default function ProductPage({ product }: { product: Product | null }) {
  return (
    <>
      <Header />
      <Container>
        {product == null ? "Product not found" : false}
        {product ? <FoundProductPage product={product} /> : false}
      </Container>
    </>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const productId = context.params.id;
  const product = await shopClient.getProduct({
    name: `/products/${productId}`,
  });
  return { props: { product: product || null } };
};

export async function getStaticPaths() {
  const paths = (
    await shopClient.listProducts({
      parent: "/products",
      pageToken: "",
      pageSize: 1000000,
    })
  ).products.map((p) => ({ params: { id: p.id } }));
  return { paths, fallback: false };
}
