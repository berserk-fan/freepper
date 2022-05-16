import React from "react";
import { GetStaticPaths, GetStaticProps } from "next";
import { shopNode } from "store";
import FoundProductPage from "components/ProductPage/FoundProductPage";
import LayoutWithHeaderAndFooter from "components/Layout/LayoutWithHeaderAndFooter";
import Box from "@material-ui/core/Box/Box";
import { Model } from "apis/model.pb";
import { Product } from "apis/product.pb";
import { Category } from "apis/category.pb";

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
      breadcrumbsOverrides={{ [model.id]: model.displayName }}
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
  const model: Model = await shopNode.getModel({
    name: `categories/${categoryId}/models/${modelId}`,
  });
  delete model.toObject;
  return { props: { model: model || null, categoryName: categoryId } };
};

export const getStaticPaths: GetStaticPaths = async () => {
  const cats = await shopNode.listCategories({
    parent: "categories",
    pageSize: 100,
  });
  const models1 = await Promise.all(
    cats.categories.map((cat) =>
      shopNode
        .listModels({
          parent: `${cat.name}/models`,
          pageSize: 100,
        })
        .then((x): [Category, Model[]] => [cat, x.models]),
    ),
  );
  const paths = models1.flatMap(([cat, ms]) =>
    ms.map((m) => ({ params: { categoryId: cat.uid, modelId: m.uid } })),
  );
  return { paths, fallback: false };
};
