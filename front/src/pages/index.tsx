import { GetStaticProps } from "next";
import { Home, HotDealsWithCategory } from "../components/Pages/Home";
import { shopNode } from "../store";

export default Home;

export const getStaticProps: GetStaticProps = async () => {
  const category = await shopNode.getCategory({
    name: `categories/5a4f57d7-aaeb-4586-9642-0d62becb0dbe`,
  });
  const { models } = await shopNode.listModels({
    parent: `${category.name}/models`,
    pageSize: 3,
  });
  const hotDealsWithCategory: HotDealsWithCategory = [category, models];
  return {
    props: {
      hotDealsWithCategory,
    },
  };
};
