import React from "react";
import { GetStaticProps } from "next";
import ShopPage from "components/Shop/ShopPage";
import "keen-slider/keen-slider.min.css";
import { Model } from "apis/model.pb";
import { shopNode } from "../../../../store";

export default function Shop(props: { models: Model[] }) {
  return <ShopPage {...props} />;
}

export const getStaticProps: GetStaticProps = async (ctx) => {
  const { categoryId } = ctx.params;
  const models1 = await shopNode.listModels({
    parent: `categories/${categoryId}/models`,
    pageSize: 10,
  });
  return {
    props: {
      products: models1,
      categoryId: `categories/${categoryId}`,
    },
  };
};

export async function getStaticPaths() {
  const categories = ["beds", "ammo"];
  const paths = categories.map((c) => ({ params: { categoryId: c } }));
  return { paths, fallback: false };
}
