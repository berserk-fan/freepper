import React from "react";
import {
  Box,
  Button,
  Theme,
  Typography,
  useTheme,
  withStyles,
} from "@material-ui/core";
import Image from "next/image";
import { GetStaticProps } from "next";
import { Product } from "@mamat14/shop-server/shop_model";
import Link from "next/link";
import { makeStyles } from "@material-ui/styles";
import { shopClient } from "../store";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";

const ColorButton = withStyles((theme: Theme) => ({
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
}))(Button);

const useStyles = makeStyles((theme: Theme) => ({
  container: {
    width: "100%",
    height: "calc(100vh - 135px)",
  },
  imageContainer: {
    overflowY: "hidden",
    width: "100%",
    height: "calc(100% - 200px)",
    minHeight: "300px",
    maxHeight: "400px",
    marginLeft: "auto",
    marginRight: "auto",
    [theme.breakpoints.up("sm")]: {
      paddingBottom: 0,
      maxHeight: "none",
      minHeight: "none",
    },
    [theme.breakpoints.up("md")]: {
      width: "calc(50% + 400px)",
    },
  },
  saleText: {
    paddingTop: theme.spacing(3),
  },
}));

const GoToShopButton = React.memo(() => {
  const theme = useTheme();
  return (
    <Link href="#">
      <ColorButton size="large" variant="contained">
        <Box
          component="span"
          color={theme.palette.getContrastText(theme.palette.secondary.main)}
        >
          В Магазин
        </Box>
      </ColorButton>
    </Link>
  );
});

export default function Home({ products }: { products: Product[] }) {
  const classes = useStyles();
  return (
    <LayoutWithHeaderAndFooter showValueProp disableBreadcrumbs>
      <Box width="100%">
        <Box
          className={`flex flex-col ${classes.container}`}
          height="calc(100vh - 135px)"
          aria-label="Main screen"
          component="section"
        >
          <Box className={`${classes.imageContainer} overflow-hidden`}>
            <Box className="relative" width="100%" height="100%">
              <Image
                priority
                src="/beds/kvadro-strong/Dogs-7248.jpg"
                alt="Заставка лежанки"
                layout="fill"
                objectFit="cover"
                quality={100}
              />
            </Box>
          </Box>
          <Box>
            <Typography
              className={classes.saleText}
              align="center"
              variant="h3"
              component="h1"
            >
              10% на первую покупку
            </Typography>
            <Box
              marginTop={2}
              marginBottom={1}
              className="flex justify-center items-center"
            >
              <GoToShopButton />
            </Box>
            <Typography align="center" variant="h5" component="h3">
              Только сегодня
            </Typography>
          </Box>
        </Box>
        <Box
          paddingY={2}
          aria-label="propositions-block"
          component="section"
          bgcolor={'grey["100"]'}
        >
          <Typography align="center" variant="h2">
            Новинки
          </Typography>
          <Box aria-label="novelties" component="ul">
            {products.map((p) => (
              <Box key={p.id} marginX={4} marginY={5} component="li">
                {/* <ItemView */}
                {/*  productRef={`/categories/beds/${p.name}`} */}
                {/*  product={p} */}
                {/* /> */}
              </Box>
            ))}
          </Box>
        </Box>
        <Box marginTop={2} aria-label="how do we make it?" component="section">
          <Box className="flex flex-col">
            <Typography variant="h2" gutterBottom>
              Как они это делают?
            </Typography>
            <Typography>
              Широкую на широкую, широкую на широкую широкую на широкую.
            </Typography>
          </Box>
          <Box className="justify-center items-center">
            <Image
              width="500px"
              height="500px"
              src="/howWeMakeIt.jpg"
              alt="фото того как делается лежанка"
            />
          </Box>
        </Box>
        <Box>
          <Box marginY={4} className="flex justify-center items-center">
            <GoToShopButton />
          </Box>
        </Box>
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}

export const getStaticProps: GetStaticProps = async (context) => {
  const productNames = [];
  const products = await Promise.all(
    productNames.map((pName) => shopClient.getProduct({ name: pName })),
  );
  return {
    props: {
      products,
    },
  };
};
