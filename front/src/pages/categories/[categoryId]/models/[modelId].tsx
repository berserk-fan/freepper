import React from "react";
import { GetStaticPaths, GetStaticProps } from "next";
import { shopNode } from "store";
import FoundProductPage from "components/ProductPage/FoundProductPage";
import LayoutWithHeaderAndFooter from "components/Layout/LayoutWithHeaderAndFooter";
import Box from "@material-ui/core/Box/Box";
import { Model } from "apis/model.pb";
import { Product } from "apis/product.pb";

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
  const { categoryId, productId } = context.params;
  const model = await shopNode.getModel({
    name: `categories/${categoryId}/model/${productId}`,
  });
  return { props: { model: model || null, categoryName: categoryId } };
};

export const getStaticPaths: GetStaticPaths = async () => {
  // TODO replace with listCategories or listProducts or listModels
  const categoryWithModelsList = await Promise.all(
    ["categories/beds", "categories/ammo"].map((categoryName) =>
      shopNode
        .listModels({ parent: `${categoryName}/models` })
        .then((resp) => ({
          categoryId: categoryName.slice("categories/".length),
          models: resp.models,
        })),
    ),
  );
  const paths = categoryWithModelsList
    .flatMap((categoryWithModels) =>
      categoryWithModels.models.map((model) => ({
        categoryId: categoryWithModels.categoryId,
        modelId: model.id,
      })),
    )
    .map((x) => ({ params: x }));
  return { paths, fallback: false };
};
