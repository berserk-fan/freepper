import React, { useState } from "react";
import Box from "@material-ui/core/Box";
import Container from "@material-ui/core/Container";
import Divider from "@material-ui/core/Divider";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import { makeStyles } from "@material-ui/core/styles";
import { ColumnToRow, Item } from "@mui-treasury/components/flex";
import { NavMenu, NavItem } from "@mui-treasury/components/menu/navigation";
import {
  CategoryProvider,
  CategoryTitle,
  CategoryItem,
} from "@mui-treasury/components/menu/category";
import {
  SocialProvider,
  SocialLink,
} from "@mui-treasury/components/socialLink";

import { useMagCategoryMenuStyles } from "@mui-treasury/styles/categoryMenu/mag";
import { usePoofSocialLinkStyles } from "@mui-treasury/styles/socialLink/poof";
import { usePlainNavigationMenuStyles } from "@mui-treasury/styles/navigationMenu/plain";
import Link from "next/link";
import ContactUsSnackBar from "../../ContactUs/ContactUsSnackBar";
import Logo from "../Logo/Logo";
import { modelPages, pages } from "../Header/pages";

const useStyles = makeStyles(({ palette, typography }) => ({
  top: {
    backgroundSize: "cover",
    overflow: "hidden",
  },
  middle: {
    backgroundColor: palette.type === "dark" ? "#192D36" : palette.action.hover,
  },
  bottom: {
    backgroundColor:
      palette.type === "dark" ? "#0F2128" : palette.action.selected,
  },
  newsletterText: {
    color: "#fff",
    TypographySize: "0.875rem",
    textTransform: "uppercase",
  },
  form: {
    margin: 0,
    minWidth: 343,
    TypographySize: "0.875rem",
  },
  legalLink: {
    textTransform: "uppercase",
    TypographyWeight: "bold",
    TypographySize: "0.75rem",
    justifyContent: "center",
    color: palette.text.hint,
    letterSpacing: "0.5px",
  },
  divider: {
    height: 2,
    margin: "-1px 0",
  },
  overlay: {
    position: "absolute",
    top: 0,
    left: 0,
    bottom: 0,
    right: 0,
    filter: "grayscale(80%)",
    "& img": {
      width: "100%",
      height: "100%",
      objectFit: "cover",
    },
  },
  info: {
    ...typography.caption,
    color: palette.text.hint,
    marginTop: 8,
    fontFamily: "Monospace"
  }
}));

const Footer = React.memo(() => {
  const classes = useStyles();
  const [contactsOpen, setContactsOpen] = useState(false);
  return (
    <Box width="100%">
      <Box px={2} pt={10} pb={10} className={classes.middle}>
        <Container disableGutters>
          <Grid container spacing={4}>
            <Grid item xs={12} md={4} lg={3}>
              <Box
                marginRight="auto"
                mt={-3}
                width="100%"
                height={120}
                borderRadius={12}
                className="center overflow-hidden"
                fontSize={400}
              >
                <Logo />
              </Box>
              <Typography className={classes.info}>
                ZooHugge, Dnipro
              </Typography>

              <Typography className={classes.info}>
                lika@pogladit-mozhno.com
              </Typography>
            </Grid>
            <Grid item xs={12} md={8} lg={6}>
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4}>
                  <CategoryProvider useStyles={useMagCategoryMenuStyles}>
                    <CategoryTitle>
                      <Typography>Модели</Typography>
                    </CategoryTitle>
                    {Object.values(modelPages).map((page) => (
                      <CategoryItem key={page.id}>
                        <Link href={pages.about.path}>
                          <Typography>{page.name}</Typography>
                        </Link>
                      </CategoryItem>
                    ))}
                  </CategoryProvider>
                </Grid>
                <Grid item xs={6} sm={4}>
                  <CategoryProvider useStyles={useMagCategoryMenuStyles}>
                    <CategoryTitle>
                      <Typography>Информация</Typography>
                    </CategoryTitle>
                    {[
                      pages.about,
                      pages["delivery-and-payment-info"],
                      pages["returns-policy"],
                    ].map((page) => (
                      <CategoryItem key={page.id}>
                        <Link href={page.path} color={"textPrimary"}>
                          <Typography>{page.name}</Typography>
                        </Link>
                      </CategoryItem>
                    ))}
                    <CategoryItem>
                      <Typography onClick={() => setContactsOpen(true)}>
                        Контакты
                      </Typography>
                    </CategoryItem>
                    <ContactUsSnackBar
                      open={contactsOpen}
                      close={() => setContactsOpen(false)}
                    />
                  </CategoryProvider>
                </Grid>
                <Grid item xs={6} sm={4}>
                  <CategoryProvider useStyles={useMagCategoryMenuStyles}>
                    <CategoryTitle>
                      <Typography>Документы</Typography>
                    </CategoryTitle>
                    {[
                      pages.cooperation,
                      pages["public-offer"],
                      pages["privacy-policy"],
                      pages.attributions,
                    ].map((page) => (
                      <CategoryItem key={page.id}>
                        <Link href={page.path} color={"textPrimary"}>
                          <Typography>{page.name}</Typography>
                        </Link>
                      </CategoryItem>
                    ))}
                  </CategoryProvider>
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs={12} md={8} lg={3} style={{ marginLeft: "auto" }}>
              <CategoryProvider useStyles={useMagCategoryMenuStyles}>
                <CategoryTitle>
                  <Typography>Подписаться</Typography>
                </CategoryTitle>
              </CategoryProvider>
              <SocialProvider useStyles={usePoofSocialLinkStyles}>
                <SocialLink brand="Envelope" />
                <SocialLink brand="Instagram" />
              </SocialProvider>
            </Grid>
          </Grid>
        </Container>
      </Box>
      <Container disableGutters>
        <Divider className={classes.divider} />
      </Container>
      <Box px={2} py={2} className={classes.bottom}>
        <Container disableGutters>
          <ColumnToRow
            at="md"
            columnStyle={{ alignItems: "center" }}
            rowStyle={{ alignItems: "unset" }}
          >
            <Item grow ml={-2} shrink={0}>
              <NavMenu useStyles={usePlainNavigationMenuStyles}>
                <ColumnToRow at="sm">
                  {[
                    pages.cooperation,
                    pages["privacy-policy"],
                  ].map((page) => (
                    <NavItem key={page.id} className={classes.legalLink}>
                      <Link href={page.path}>
                        <Typography align="center">{page.name}</Typography>
                      </Link>
                    </NavItem>
                  ))}
                </ColumnToRow>
              </NavMenu>
            </Item>
            <Item>
              <Box py={1} textAlign={{ xs: "center", md: "right" }}>
                <Typography
                  component="p"
                  variant="caption"
                  color="textSecondary"
                >
                  <Box component="span" fontFamily="Monospace">
                    Designed by Dima © Home Studio 2021 All right reserved
                  </Box>
                </Typography>
              </Box>
            </Item>
          </ColumnToRow>
        </Container>
      </Box>
    </Box>
  );
});

export default Footer;
