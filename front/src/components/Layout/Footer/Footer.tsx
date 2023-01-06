import React, { useState } from "react";
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Divider from "@mui/material/Divider";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import Link from "next/link";
import { useStyles } from "components/Layout/Footer/styles";
import MailOutline from "@mui/icons-material/MailOutline";
import Instagram from "@mui/icons-material/Instagram";
import ContactUsSnackBar from "../../ContactUs/ContactUsSnackBar";
import Logo from "../Logo/Logo";
import { modelPages, pages } from "../Header/pages";

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
                width={120}
                height={120}
                borderRadius="12px"
                className="center overflow-hidden"
              >
                <Logo />
              </Box>
              <Typography className={classes.info}>Freepper, Dnipro</Typography>
              <Typography className={classes.info}>
                dima@freepper.com
              </Typography>
            </Grid>
            <Grid item xs={12} md={8} lg={6}>
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4}>
                  <Typography component="h6" className={classes.title}>
                    Модели
                  </Typography>
                  {Object.values(modelPages).map((page) => (
                    <Link key={page.id} href={pages.about.path}>
                      <a className={classes.item}>
                        <Typography>{page.name}</Typography>
                      </a>
                    </Link>
                  ))}
                </Grid>
                <Grid item xs={6} sm={4}>
                  <Typography component="h6" className={classes.title}>
                    Информация
                  </Typography>
                  {[
                    pages.about,
                    pages["delivery-and-payment-info"],
                    pages["returns-policy"],
                    pages["ua-version"],
                  ].map((page) => (
                    <Link key={page.id} href={page.path}>
                      <a className={classes.item}>
                        <Typography>{page.name}</Typography>
                      </a>
                    </Link>
                  ))}
                  <a className={classes.item}>
                    <Typography onClick={() => setContactsOpen(true)}>
                      Контакты
                    </Typography>
                  </a>
                  <ContactUsSnackBar
                    open={contactsOpen}
                    close={() => setContactsOpen(false)}
                  />
                </Grid>
                <Grid item xs={6} sm={4}>
                  <Typography component="h6" className={classes.title}>
                    Документы
                  </Typography>
                  {[
                    pages.cooperation,
                    pages["public-offer"],
                    pages["privacy-policy"],
                    pages.attributions,
                  ].map((page) => (
                    <Link key={page.id} href={page.path}>
                      <a className={classes.item}>
                        <Typography>{page.name}</Typography>
                      </a>
                    </Link>
                  ))}
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs={12} md={8} lg={3} style={{ marginLeft: "auto" }}>
              <Typography component="h6" className={classes.title}>
                Подписаться
              </Typography>
              <a
                className={classes.anchor}
                target="_blank"
                rel="noopener noreferrer"
              >
                <MailOutline />
              </a>
              <a
                className={classes.anchor}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Instagram />
              </a>
            </Grid>
          </Grid>
        </Container>
      </Box>
      <Container disableGutters>
        <Divider className={classes.divider} />
      </Container>
      <Box px={2} py={2} className={classes.bottom}>
        <Container disableGutters>
          <Box display="flex" flexDirection={{ xs: "column", md: "row" }}>
            <Box ml={-2}>
              <div className={classes.menu}>
                <Box display="flex" flexWrap="wrap">
                  {[pages.cooperation, pages["privacy-policy"]].map((page) => (
                    <Link key={page.id} href={page.path}>
                      <a className={classes.menuItem}>
                        <Typography align="center">{page.name}</Typography>
                      </a>
                    </Link>
                  ))}
                </Box>
              </div>
            </Box>
            <Box py={1} flexGrow={1} textAlign={{ xs: "center", md: "right" }}>
              <Typography component="p" variant="caption" color="textSecondary">
                <Box component="span" fontFamily="Monospace">
                  Designed{" "}
                  <span style={{ textDecorationLine: "line-through" }}>
                    in California
                  </span>{" "}
                  in Dnipro and Kyiv. © Dimas Home Studio
                  <br />
                  2023 All rights reserved
                </Box>
              </Typography>
            </Box>
          </Box>
        </Container>
      </Box>
    </Box>
  );
});

export default Footer;
