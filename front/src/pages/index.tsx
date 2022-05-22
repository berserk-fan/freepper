import { GetStaticProps } from "next";
import { Home, HotDealsWithCategory } from "../components/Pages/Home";
import { shopNode } from "../store";

export default Home;

export const getStaticProps: GetStaticProps = async () => {
  const category = await shopNode.getCategory({
    name: "categories/beds",
  });
  const { models } = await shopNode.listModels({
    parent: `categories/beds/models`,
    pageSize: 3,
  });
  delete (category as any).toObject;
  const hotDealsWithCategory: HotDealsWithCategory = [category, models];
  return {
    props: {
      hotDealsWithCategory,
    },
  };
};
