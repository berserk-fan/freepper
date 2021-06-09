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
import Grid from "@material-ui/core/Grid";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";
import { fade } from '@material-ui/core/styles/colorManipulator';
import {useMediaQuery} from "@material-ui/core";

const ColorButton = withStyles((theme) => ({
  root: {
    fontWeight: 700,
    fontSize: "1rem",
    paddingLeft: "14px",
    paddingRight: "14px",
    width: "200px",
    height: "50px",
    [theme.breakpoints.up("sm")]: {
      width: "300px",
      height: "80px",
      fontSize: "1.4rem",
    },
  },
}))(Button);

const useStyles = makeStyles((theme) => ({
  homeImage: {
    width: "100%",
    zIndex: -1,
    height: "40%",
    position: "relative",
    [theme.breakpoints.up("sm")]: {
      position: "absolute",
      height: "100%",
    },
  },
  chooseButtonConteiner: {
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(1),
    [theme.breakpoints.up("sm")]: {
      marginTop: theme.spacing(4),
      marginBottom: theme.spacing(3),
    },
  },
  textContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "justify-start",
    height: "60%",
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(2),
    marginLeft: theme.spacing(1.5),
    marginRight: theme.spacing(1.5),
    [theme.breakpoints.up("sm")]: {
      height: "auto",
      padding: theme.spacing(2),
      margin: theme.spacing(4),
      borderRadius: "3px",
    },
  },
  mainText: {
    padding: theme.spacing(1),
    [theme.breakpoints.up("sm")]: {
      backgroundColor: theme.palette.primary.light,
    },
  },
  profitText: {
    [theme.breakpoints.up("sm")]: {
      backgroundColor: theme.palette.primary.light,
    },
  },
  propositionImageContainer: {
    width: "100%",
    paddingTop: "100%",
    position: "relative",
  },
}));

const GoToShopButton = React.memo(() => (
  <Link href="#">
    <ColorButton color="secondary" size="large" variant="contained">
      Выбрать лежанку
    </ColorButton>
  </Link>
));

const propositions = [
  { src: "/beds/lukoshko3/Dogs-24838.jpg", alt: "Лежанка 1", href: "#" },
  { src: "/beds/lukoshko3/Dogs-24838.jpg", alt: "Лежанка 2", href: "#" },
  { src: "/beds/lukoshko3/Dogs-24838.jpg", alt: "Лежанка 3", href: "#" },
];

export default function Home({ products }: { products: Product[] }) {
  const classes = useStyles();
  return (
    <LayoutWithHeaderAndFooter showValueProp disableBreadcrumbs>
      <Box width="100%">
        <Box
          className="flex flex-col items-center justify-center"
          position="relative"
          height="calc(100vh - 139px)"
          aria-label="Main screen"
          component="section"
        >
          <Box
            display={{ xs: "block", md: "none" }}
            className={classes.homeImage}
          >
            <Image
              priority
              src="/main-image.jpg"
              alt="Заставка лежанки"
              layout="fill"
              objectFit="cover"
              sizes="100vw"
              quality={85}
            />
          </Box>
          <Box
            display={{ xs: "none", md: "block" }}
            className={classes.homeImage}
          >
            <Image
              priority
              src="/main-image-full-width.jpg"
              alt="Заставка лежанки"
              layout="fill"
              objectFit="cover"
              sizes="100vw"
              quality={85}
            />
          </Box>
          <Box className={classes.textContainer} fontWeight={900}>
            <Typography
              align="center"
              variant="h3"
              component="h1"
              className={classes.mainText}
            >
              УДОБСТВО. СТИЛЬ. ЛЕЖАНКИ
            </Typography>
            <Box
              className={`flex justify-center items-center ${classes.chooseButtonConteiner}`}
            >
              <GoToShopButton />
            </Box>
            <Box
              padding={1}
              display="inline-block"
              marginX="auto"
              className={classes.profitText}
            >
              <Typography align="center" variant="h5" component="h2">
                Бесплатная доставка. Гарантия 2 месяца.
              </Typography>
            </Box>
          </Box>
        </Box>
        <Box
          paddingY={2}
          aria-label="propositions"
          component="section"
          bgcolor={'grey["100"]'}
        >
          <Typography align="center" variant="h2">
            Лежанки
          </Typography>
          <Box
            marginX="auto"
            aria-label="лежанки"
            component="ul"
            display="flex"
            flexDirection="row"
            justifyContent="space-around"
            maxWidth="1600px"
          >
            <Grid container spacing={3}>
              {propositions.map((proposition) => (
                <Grid item key={1} xs={12} md={4}>
                  <Box className={classes.propositionImageContainer}>
                    <Image
                      src={proposition.src}
                      alt={proposition.alt}
                      layout="fill"
                      objectFit="cover"
                    />
                  </Box>
                  <Typography
                    gutterBottom
                    variant="h4"
                    component="h3"
                    align="center"
                  >
                    {proposition.alt}
                  </Typography>
                </Grid>
              ))}
            </Grid>
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
