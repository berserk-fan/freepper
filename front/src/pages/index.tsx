import { GetStaticProps } from "next";
import { Product } from "apis/catalog";
import { Home, HotDealsWithCategory } from "../components/Pages/Home";
import { all_products } from "../configs/catalog/beds";
import { shopClient } from "../store";

export default Home;

export const getStaticProps: GetStaticProps = async () => {
  const category = await shopClient.getCategory({ name: "categories/beds" });
  const products: Product[] = Object.values(
    all_products.reduce<{ [key: string]: Product }>(
      (prev, cur) => ({ ...prev, ...{ [cur.modelId]: cur } }),
      {},
    ),
  ).slice(0, 3);

  const hotDealsWithCategory: HotDealsWithCategory = [category, products];
  return {
    props: {
      hotDealsWithCategory,
    },
  };
};
