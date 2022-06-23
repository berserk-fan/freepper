import React from "react";
import { GetStaticPaths, GetStaticProps } from "next";
import grpcClient from "commons/shopClient";
import FoundProductPage from "components/ProductPage/FoundProductPage";
import LayoutWithHeaderAndFooter from "components/Layout/LayoutWithHeaderAndFooter";
import Box from "@mui/material/Box/Box";
import { Model } from "apis/model.pb";
import { Product } from "apis/product.pb";
import { Category } from "apis/category.pb";
import { removeUndefined } from "../../../../commons/utils";

export default function ProductPage({
  model,
  products,
}: {
  model: Model | null;
  products: Product[] | null;
}) {
  return (
    <LayoutWithHeaderAndFooter
      showValueProp
      breadcrumbsOverrides={{ [model.uid]: model.displayName }}
    >
      <Box>
        {!model && "Model not found"}
        {model && <FoundProductPage model={model} products={products} />}
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const { categoryId, modelId } = context.params;
  const modelName = `categories/${categoryId}/models/${modelId}`;
  const model: Model = await grpcClient().getModel({
    name: modelName,
  });
  const products: Product[] = await grpcClient()
    .listProducts({
      parent: `${modelName}/products`,
      pageSize: 200,
    })
    .then((x) => x.products);
  return removeUndefined({
    props: { model: model || null, products },
  });
};

export const getStaticPaths: GetStaticPaths = async () => {
  const cats = await grpcClient().listCategories({
    parent: "categories",
    pageSize: 100,
  });
  const models1 = await Promise.all(
    cats.categories.map((cat) =>
      grpcClient()
        .listModels({
          parent: `${cat.name}/models`,
          pageSize: 100,
        })
        .then((x): [Category, Model[]] => [cat, x.models]),
    ),
  );
  const paths = models1.flatMap(([cat, ms]) =>
    ms.map((m) => ({ params: { categoryId: cat.readableId, modelId: m.uid } })),
  );
  return { paths, fallback: false };
};
