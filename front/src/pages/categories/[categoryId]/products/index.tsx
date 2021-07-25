import React from "react";
import { GetStaticProps } from "next";
import ShopPage, { ShopPageProps } from "components/Shop/ShopPage";
import "keen-slider/keen-slider.min.css";
import { models } from "configs/catalog/beds";
import { modelToCategory } from "../../../../configs/catalog/defs";

export default function Shop(props: ShopPageProps) {
  return <ShopPage {...props} />;
}

export const getStaticProps: GetStaticProps = async (ctx) => {
  const { categoryId } = ctx.params;
  const models1 = models.filter((m) => modelToCategory[m.id] === categoryId);
  return {
    props: {
      products: models1,
      categoryName: `categories/${categoryId}`,
    },
  };
};

export async function getStaticPaths() {
  const categories = ["beds", "ammo"];
  const paths = categories.map((c) => ({ params: { categoryId: c } }));
  return { paths, fallback: false };
}
