import { GetStaticProps } from "next";
import { Product } from "apis/product.pb";
import { Home, HotDealsWithCategory } from "../components/Pages/Home";
import { shopNode } from "../store";

export default Home;

export const getStaticProps: GetStaticProps = async () => {
  const category = await shopNode.getCategory({ name: `categories/5a4f57d7-aaeb-4586-9642-0d62becb0dbe` });
  const products: Product[] = [];
  const hotDealsWithCategory: HotDealsWithCategory = [category, products];
  return {
    props: {
      hotDealsWithCategory,
    },
  };
};
