import React from "react";
import { GetStaticProps } from "next";
import ShopPage from "components/Shop/ShopPage";
import "keen-slider/keen-slider.min.css";
import { Model } from "apis/model.pb";
import grpcClient from "commons/shop-node";
import { removeUndefined } from "../../../../commons/utils";

export default function Shop(props: { models: Model[] }) {
  return <ShopPage {...props} />;
}

export const getStaticProps: GetStaticProps = async (ctx) => {
  const { categoryId } = ctx.params;
  const models1 = await grpcClient().listModels({
    parent: `categories/${categoryId}/models`,
    pageSize: 10,
  });

  return removeUndefined({
    props: {
      models: JSON.parse(JSON.stringify(models1.models)),
    },
  });
};

export async function getStaticPaths() {
  const categories = await grpcClient().listCategories({
    parent: "categories",
    pageSize: 25,
  });
  const paths = categories.categories.map((c) => ({
    params: { categoryId: c.readableId },
  }));
  return { paths, fallback: false };
}
