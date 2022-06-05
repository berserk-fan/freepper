import withStyles from "@material-ui/styles/withStyles";
import Button from "@material-ui/core/Button/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import React from "react";
import Link from "next/link";
import Box from "@material-ui/core/Box/Box";
import Image from "next/image";
import Typography from "@material-ui/core/Typography/Typography";
import Container from "@material-ui/core/Container";
import Grid from "@material-ui/core/Grid";
import { Model } from "apis/model.pb";
import { Category } from "apis/category.pb";
import LayoutWithHeaderAndFooter from "../Layout/LayoutWithHeaderAndFooter";

const ColorButton = withStyles((theme) => ({
  root: {
    fontWeight: 700,
    fontSize: "1rem",
    paddingLeft: "14px",
    paddingRight: "14px",
    width: "200px",
    height: "50px",
    [theme.breakpoints.up("md")]: {
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

export type HotDealsWithCategory = [Category, Model[]];

export function Home({
  hotDealsWithCategory: [hotDealsCategory, hotDeals],
}: {
  hotDealsWithCategory: HotDealsWithCategory;
}) {
  const classes = useStyles();
  const allBedsHref = `categories/beds/models`;

  return (
    <LayoutWithHeaderAndFooter disableBreadcrumbs showValueProp>
      <Box width="100%">
        <Box
          className="flex flex-col items-center justify-center"
          position="relative"
          height="calc(100vh - 130px)"
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
              Лучшие лежанки в интернете
            </Typography>
            <Box
              className={`flex justify-center items-center ${classes.chooseButtonConteiner}`}
            >
              <Link href={allBedsHref}>
                <ColorButton color="secondary" size="large" variant="contained">
                  Выбрать лежанку
                </ColorButton>
              </Link>
            </Box>
            <Box padding={0.5} marginX="auto" className={classes.profitText}>
              <Typography align="center" variant="h5" component="h2">
                Бесплатная доставка. Гарантия 2 месяца.
              </Typography>
            </Box>
          </Box>
        </Box>
        <Container>
          <Box
            padding={2}
            aria-label="propositions"
            component="section"
            bgcolor={'grey["100"]'}
          >
            <Box marginTop={10} marginBottom={7}>
              <Typography align="center" variant="h2">
                Избранные {hotDealsCategory.displayName}
              </Typography>
            </Box>
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
                {hotDeals.map((model: Model) => {
                  const href = `/${model.name}`;
                  const image = model.imageList.images[0];
                  return (
                    <Grid item key={model.name} xs={12} sm={6} md={4}>
                      <Box className={classes.propositionImageContainer}>
                        <Link href={href}>
                          <Image
                            src={image.src}
                            alt={image.alt}
                            layout="fill"
                            objectFit="cover"
                          />
                        </Link>
                      </Box>
                      <Typography
                        gutterBottom
                        variant="h4"
                        component="h3"
                        align="center"
                      >
                        {model.displayName}
                      </Typography>
                    </Grid>
                  );
                })}
              </Grid>
            </Box>
          </Box>
          <Box marginY={10} display="flex" justifyContent="center">
            <Link href={allBedsHref}>
              <ColorButton color="secondary" size="large" variant="contained">
                Все лежанки
              </ColorButton>
            </Link>
          </Box>
        </Container>
      </Box>
    </LayoutWithHeaderAndFooter>
  );
}
