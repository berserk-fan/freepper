import React from "react";
import { GetStaticProps } from "next";
import { models } from "configs/catalog/beds";
import ShopPage, { ShopPageProps } from "components/Shop/ShopPage";
import "keen-slider/keen-slider.min.css";

export default function Shop(props: ShopPageProps) {
  return <ShopPage {...props} />;
}

export const getStaticProps: GetStaticProps = async () => {
  const products = models;
  const category = "categories/beds";
  return {
    props: {
      products,
      categoryName: category,
    },
  };
};

export async function getStaticPaths() {
  const categories = ["beds"];
  const paths = categories.map((c) => ({ params: { categoryId: c } }));
  return { paths, fallback: false };
}
