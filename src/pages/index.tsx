import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";
import React from "react";
import {
  Box,
  Button,
  Divider,
  Grid,
  Typography,
  withStyles,
} from "@material-ui/core";
import theme from "../theme";
import Image from "next/image";
import ItemView from "../components/Shop/ItemView";
import { GetStaticProps } from "next";
import { shopClient } from "../store";
import { Product } from "@mamat14/shop-server/shop_model";
import Link from "next/link";
import { pages } from "../components/Layout/Header/Header";

export type CardData = {
  id: string;
  src: string;
  title: string;
  text: string;
  alt: string;
};

const ColorButton = withStyles({
  root: {
    color: theme.palette.getContrastText(theme.palette.secondary.light),
    backgroundColor: theme.palette.secondary.main,
    fontWeight: 400,
    paddingLeft: "14px",
    paddingRight: "14px",
    "&:hover": {
      backgroundColor: theme.palette.secondary.main,
    },
  },
})(Button);

const goToShopButton = () => (
  <Link href={"#"}>
    <ColorButton size={"large"} variant={"contained"}>
      <span
        style={{
          color: theme.palette.getContrastText(theme.palette.secondary.main),
        }}
      >
        {" "}
        В Магазин{" "}
      </span>
    </ColorButton>
  </Link>
);

export default function Home({ products }: { products: Product[] }) {
  return (
    <LayoutWithHeaderAndFooter value>
      <Box maxWidth={"500px"}>
        <Box height="100vh" aria-label={"Main screen"} component={"section"}>
          <Box
            height={"52%"}
            className={"flex items-center justify-center overflow-hidden"}
          >
            <Image
              priority
              height={"500px"}
              width={"500px"}
              src={"/IMG_4337.JPG"}
              alt={"Заставка лежанки"}
            />
          </Box>
          <Typography
            style={{ paddingTop: theme.spacing(3) }}
            align={"center"}
            variant={"h3"}
            component={"h1"}
          >
            10% на первую покупку
          </Typography>
          <Box
            marginTop={2}
            marginBottom={1}
            className={"flex justify-center items-center"}
          >
            {goToShopButton()}
          </Box>
          <Typography align={"center"} variant={"h5"} component={"h3"}>
            Только сегодня
          </Typography>
        </Box>
        <Box
          paddingY={2}
          aria-label={"propositions-block"}
          component={"section"}
          bgcolor={theme.palette.grey["100"]}
        >
          <Typography align={"center"} variant={"h2"}>
            Новинки
          </Typography>
          <Box aria-label={"novelties"} component={"ul"}>
            {products.map((p) => (
              <Box marginX={4} marginY={5} component={"li"}>
                <ItemView product={p} />
              </Box>
            ))}
          </Box>
        </Box>
        <Box
          marginTop={2}
          aria-label={"how do we make it?"}
          component={"section"}
        >
          <Box className={"flex flex-col"}>
            <Typography variant={"h2"} gutterBottom>
              Как они это делают?
            </Typography>
            <Typography>
              Широкую на широкую, широкую на широкую широкую на широкую.
            </Typography>
          </Box>
          <Box className={"justify-center items-center"}>
            <Image
              width={"500px"}
              height={"500px"}
              src={"/howWeMakeIt.jpg"}
              alt={"фото того как делается лежанка"}
            />
          </Box>
        </Box>
        <Box>
          <Box marginY={4} className={"flex justify-center items-center"}>
            {goToShopButton()}
          </Box>
        </Box>
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const productNames = ["/products/lukoshko-500-1", "/products/lukoshko-500-2"];
  const products = await Promise.all(
    productNames.map((pName) => shopClient.getProduct({ name: pName }))
  );
  return {
    props: {
      products: products,
    },
  };
};
