import { GetStaticProps } from "next";
import { Home, HotDealsWithCategory } from "../components/Pages/Home";
import { shopNode } from "../store";

export default Home;

export const getStaticProps: GetStaticProps = async () => {
  const listCategoriesResponse = await shopNode.listCategories({
    parent: "categories",
    pageSize: 25,
  });
  const category = listCategoriesResponse.categories[0];
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
