import React from "react";
import Image from "next/image";
import { GetStaticProps } from "next";
import { Product } from "@mamat14/shop-server/shop_model";
import Link from "next/link";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { shopClient } from "store";
import withStyles from "@material-ui/styles/withStyles";
import Button from "@material-ui/core/Button/Button";
import useTheme from "@material-ui/core/styles/useTheme";
import Box from "@material-ui/core/Box/Box";
import Typography from "@material-ui/core/Typography/Typography";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";
import { fade } from '@material-ui/core/styles/colorManipulator';

const ColorButton = withStyles((theme) => ({
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

const useStyles = makeStyles((theme) => ({
  saleText: {
    paddingTop: theme.spacing(3),
  },
  homeImage: {
    width: "100%",
    zIndex: -1,
    height: "70%",
    position: "relative",
    [theme.breakpoints.up("sm")]: {
      position: "absolute",
      height: "100%",
    }
  },
  mainText: {
    height: "30%",
    [theme.breakpoints.up("sm")]: {
      height: "auto",
      backgroundColor: fade(theme.palette.grey.A100, 0.8),
      padding: theme.spacing(4),
      borderRadius: "10px"
    }
  }
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
  const theme = useTheme();
  return (
    <LayoutWithHeaderAndFooter showValueProp disableBreadcrumbs>
      <Box width="100%">
        <Box
          className={`flex flex-col items-center justify-center`}
          position={"relative"}
          height="calc(100vh - 139px)"
          aria-label="Main screen"
          component="section"
        >
          <Box className={classes.homeImage}>
            <Box display={{xs: "none"}}></Box>
            <Image
              priority
              src="/main-image.jpg"
              alt="Заставка лежанки"
              layout="fill"
              objectFit="cover"
              sizes={"100vw"}
              quality={100}
            />
          </Box>
          <Box className={classes.mainText}>
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
